package OdiAtjar;

import java.util.Iterator;
import java.util.Collection;
import oracle.odi.domain.IOdiEntity;
import oracle.odi.domain.model.finder.IOdiKeyFinder;
import oracle.odi.domain.model.finder.IOdiColumnFinder;
import oracle.odi.domain.model.OdiModel;
import oracle.odi.domain.model.finder.IOdiModelFinder;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.model.OdiKey;
import oracle.odi.domain.model.OdiColumn;
import oracle.odi.domain.model.OdiDataStore;

public class Model extends Connection
{
    private OdiDataStore trgDatastore;
    private OdiDataStore trgFileDatastore;
    private OdiColumn trgCol;
    private OdiKey sdkPrimaryKey;
    
    public Model(final String purl, final String pdriver, final String pschema, final String pschemapwd, final String pworkrep, final String podiuser, final String podiuserpwd) {
        super(purl, pdriver, pschema, pschemapwd, pworkrep, podiuser, podiuserpwd);
        this.sdkPrimaryKey = null;
    }
    
    public Model(final String pxml) throws Exception {
        super(pxml);
        this.sdkPrimaryKey = null;
    }
    
    public void Create(final String pdatastore, final String pModelname, final String pscd) {
        OdiDataStore trgDatastorefd = null;
        trgDatastorefd = ((IOdiDataStoreFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiDataStore.class)).findByName(pModelname, pdatastore);
        if (trgDatastorefd == null) {
            final OdiModel odiModel = ((IOdiModelFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiModel.class)).findByCode(pdatastore);
            (this.trgDatastore = new OdiDataStore(odiModel, pModelname)).setDefaultAlias(pModelname);
        }
        else {
            System.out.println(trgDatastorefd.getName());
            (this.trgDatastore = trgDatastorefd).setDefaultAlias(pModelname);
        }
        if (pscd.equals("SIM")) {
            this.trgDatastore.setOlapType(OdiDataStore.OlapType.SLOWLY_CHANGING_DIMENSION);
        }
    }
    
    public void Create(final String pmodelname, final String pdatastore, final String pseparator, final String pdemiliter, final String pRowSeparator, final int pSkip, final String pDecimalSeparator) {
        final OdiDataStore trgDatastorefd = ((IOdiDataStoreFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiDataStore.class)).findByName(pmodelname, pdatastore);
        if (trgDatastorefd == null) {
            final OdiModel odiModel = ((IOdiModelFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiModel.class)).findByCode(pdatastore);
            (this.trgFileDatastore = new OdiDataStore(odiModel, pmodelname)).setFileDescriptor(new OdiDataStore.FileDescriptor(OdiDataStore.FileDescriptor.Format.FIXED, pseparator, pdemiliter, pRowSeparator, pSkip, pDecimalSeparator));
            this.trgFileDatastore.setDefaultAlias(pmodelname);
        }
        else {
            (this.trgFileDatastore = trgDatastorefd).setDefaultAlias(pmodelname);
        }
    }
    
    public void AddModelColumn(final String pcolumnname, final String ptype, final int plength, final int pscale, final String pDescription) {
        final OdiColumn odiColumn = ((IOdiColumnFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiColumn.class)).findByName(pcolumnname, this.trgFileDatastore.getDataStoreId());
        OdiColumn trgCol;
        if (odiColumn == null) {
            trgCol = new OdiColumn(this.trgFileDatastore, pcolumnname.toUpperCase());
        }
        else {
            trgCol = odiColumn;
        }
        trgCol.setDataTypeCode(ptype);
        trgCol.setLength(Integer.valueOf(plength));
        trgCol.setScale(Integer.valueOf(pscale));
        trgCol.setDescription(pDescription);
    }
    
    public void RemovePK(final String pmodelname, final String pdatastore) {
        final OdiDataStore trgDatastorefd = ((IOdiDataStoreFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiDataStore.class)).findByName(pmodelname, pdatastore);
        if (trgDatastorefd != null) {
            final OdiKey sdkPrimaryKeyfd = ((IOdiKeyFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiKey.class)).findByName("PK_" + trgDatastorefd.getName(), trgDatastorefd.getDataStoreId());
            if (sdkPrimaryKeyfd != null) {
                this.odiInstance.getTransactionalEntityManager().remove((IOdiEntity)sdkPrimaryKeyfd);
                System.out.println("Interface PK_" + trgDatastorefd.getName() + " sucessful removed!");
            }
            else {
                System.out.println("PK_" + trgDatastorefd.getName());
                System.out.println("Primary Key doesn't exists!");
            }
        }
    }
    
    public void AddModelColumn(final String pcolumnname, final String ptype, final boolean pmandatory, final int plength, final int pscale, final boolean pk, final String pscd, final String pdescription) {
        final OdiColumn odiColumn = ((IOdiColumnFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiColumn.class)).findByName(pcolumnname, this.trgDatastore.getDataStoreId());
        if (odiColumn == null) {
            this.trgCol = new OdiColumn(this.trgDatastore, pcolumnname.toUpperCase());
            this.odiInstance.getTransactionalEntityManager().persist((IOdiEntity)this.trgCol);
        }
        else {
            this.trgCol = odiColumn;
        }
        this.trgCol.setDataTypeCode(ptype);
        this.trgCol.setMandatory(pmandatory);
        this.trgCol.setLength(Integer.valueOf(plength));
        this.trgCol.setScale(Integer.valueOf(pscale));
        this.trgCol.setDescription(pdescription);
        if (!pscd.equals("")) {
            if (pscd.toUpperCase().equals("ADD")) {
                this.trgCol.setScdType(OdiColumn.ScdType.ADD_ROW_ON_CHANGE);
            }
            else if (pscd.toUpperCase().equals("UPDATE")) {
                this.trgCol.setScdType(OdiColumn.ScdType.OVERWRITE_ON_CHANGE);
            }
            else if (pscd.toUpperCase().equals("FLAG")) {
                this.trgCol.setScdType(OdiColumn.ScdType.CURRENT_RECORD_FLAG);
            }
            else if (pscd.toUpperCase().equals("END")) {
                this.trgCol.setScdType(OdiColumn.ScdType.END_TIMESTAMP);
            }
            else if (pscd.toUpperCase().equals("START")) {
                this.trgCol.setScdType(OdiColumn.ScdType.START_TIMESTAMP);
            }
            else if (pscd.toUpperCase().equals("SK")) {
                this.trgCol.setScdType(OdiColumn.ScdType.SURROGATE_KEY);
            }
            else if (pscd.toUpperCase().equals("NK")) {
                this.trgCol.setScdType(OdiColumn.ScdType.NATURAL_KEY);
            }
        }
        if (pk) {
            final OdiKey sdkPrimaryKeyfd = ((IOdiKeyFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiKey.class)).findByName("PK_" + this.trgDatastore.getName(), this.trgDatastore.getDataStoreId());
            boolean found = false;
            if (sdkPrimaryKeyfd == null && this.sdkPrimaryKey == null) {
                System.out.println(this.trgDatastore.getDataStoreId());
                this.sdkPrimaryKey = new OdiKey(this.trgDatastore, "PK_" + this.trgDatastore.getName());
                final OdiKey.KeyType keyType = OdiKey.KeyType.valueOf("PRIMARY_KEY");
                System.out.println(this.sdkPrimaryKey.getName());
                System.out.println(this.sdkPrimaryKey.getDataStore().getName());
                this.odiInstance.getTransactionalEntityManager().persist((IOdiEntity)this.sdkPrimaryKey);
                this.sdkPrimaryKey.setKeyType(keyType);
                this.sdkPrimaryKey.addColumn(this.trgCol);
            }
            else {
                this.sdkPrimaryKey = ((sdkPrimaryKeyfd != null) ? sdkPrimaryKeyfd : this.sdkPrimaryKey);
                final Collection<OdiColumn> odicolumns = (Collection<OdiColumn>)this.sdkPrimaryKey.getColumns();
                for (final OdiColumn column : odicolumns) {
                    if (column.getName().equals(this.trgCol.getName())) {
                        found = true;
                    }
                }
                if (!found) {
                    this.sdkPrimaryKey.addColumn(this.trgCol);
                }
            }
        }
    }
    
    public void Save_model() {
        this.odiInstance.getTransactionalEntityManager().persist((IOdiEntity)this.trgDatastore);
    }
}