package OdiAtjar;

import oracle.odi.interfaces.interactive.support.actions.InterfaceActionSetKMOptionValue;
import oracle.odi.domain.project.OdiLKM;
import oracle.odi.domain.project.finder.IOdiLKMFinder;
import oracle.odi.domain.project.OdiCKM;
import oracle.odi.domain.project.finder.IOdiCKMFinder;
import oracle.odi.interfaces.interactive.support.IKMOptionRetainer;
import oracle.odi.domain.project.interfaces.IInterfaceSubComponent;
import oracle.odi.domain.project.OdiKM;
import oracle.odi.interfaces.interactive.support.km.optionretainer.KMOptionRetainerLazy;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionSetKM;
import oracle.odi.domain.project.OdiIKM;
import oracle.odi.domain.project.finder.IOdiIKMFinder;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTargetColumnSetBusinessRule;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTargetColumnSetIndicator;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTargetMappingSetSql;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionAddFilter;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionAddLookup;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnJoinSetJoinProperties;
import oracle.odi.domain.project.interfaces.Join;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTargetColumnSetScale;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTargetColumnSetLength;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTargetColumnSetDataType;
import oracle.odi.domain.topology.OdiDataType;
import oracle.odi.domain.topology.finder.IOdiDataTypeFinder;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTemporaryTargetDataStoreAddColumn;
import oracle.odi.interfaces.interactive.support.mapping.automap.AutoMappingComputerColumnName;
import oracle.odi.interfaces.interactive.support.ITargetKeyChooser;
import oracle.odi.interfaces.interactive.support.IAutoMappingComputer;
import oracle.odi.interfaces.interactive.IMappingMatchPolicy;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionSetTargetDataStore;
import oracle.odi.interfaces.interactive.support.targetkeychoosers.TargetKeyChooserPrimaryKey;
import oracle.odi.interfaces.interactive.support.mapping.automap.AutoMappingComputerLazy;
import oracle.odi.interfaces.interactive.support.mapping.matchpolicy.MappingMatchPolicyLazy;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTemporaryTargetDataStoreSetDatabaseSchema;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTemporaryTargetDataStoreSetName;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnSourceDataStoreSetAlias;
import oracle.odi.domain.project.interfaces.SourceDataStore;
import oracle.odi.domain.model.OdiDataStore;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.interfaces.interactive.IInterfaceAction;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnStagingAreaSetLogicalSchema;
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder;
import oracle.odi.domain.IOdiEntity;
import oracle.odi.domain.project.finder.IOdiInterfaceFinder;
import java.util.Iterator;
import java.util.Collection;
import oracle.odi.domain.topology.finder.IOdiContextFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.interfaces.DataSet;
import oracle.odi.domain.topology.OdiLogicalSchema;
import oracle.odi.interfaces.interactive.support.InteractiveInterfaceHelperWithActions;
import oracle.odi.domain.topology.OdiContext;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiInterface;

public class Interface extends Connection
{
    private OdiInterface odiInterface;
    private OdiFolder odiFolder;
    private OdiContext context;
    private InteractiveInterfaceHelperWithActions interactiveHelper;
    private OdiLogicalSchema oracleLogicalSchema;
    private DataSet dataSet;
    private String project;
    
    @SuppressWarnings("rawtypes")
	public void Create(final String pproject, final String podifolder, final String podiparfolder, final String pcontextcode, final String pname) {
        this.project = pproject;
        final Collection<OdiFolder> odiFolders = (Collection<OdiFolder>)((IOdiFolderFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiFolder.class)).findByName(podifolder, this.project);
        if (podiparfolder.equals("")) {
            for (final OdiFolder folder1 : odiFolders) {
                this.odiFolder = folder1;
            }
        }
        else {
            for (final OdiFolder folder2 : odiFolders) {
                if (folder2.getParentFolder().getName().equals(podiparfolder)) {
                    this.odiFolder = folder2;
                }
            }
        }
        this.context = ((IOdiContextFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiContext.class)).findByCode(pcontextcode);
        this.odiInterface = new OdiInterface(this.odiFolder, pname, this.context);
        this.interactiveHelper = new InteractiveInterfaceHelperWithActions(this.odiInterface, this.odiInstance, this.odiInstance.getTransactionalEntityManager());
        this.dataSet = this.odiInterface.getDataSets().get(0);
    }
    
    public Collection<OdiInterface> getInterface(final String pproject, final String podifolder, final String podiparfolder, final String pcontextcode, final String pname) {
        this.project = pproject;
        final Collection<OdiFolder> odiFolders = (Collection<OdiFolder>)((IOdiFolderFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiFolder.class)).findByName(podifolder, this.project);
        if (podiparfolder.equals("")) {
            for (final OdiFolder folder1 : odiFolders) {
                this.odiFolder = folder1;
            }
        }
        else {
            for (final OdiFolder folder2 : odiFolders) {
                if (folder2.getParentFolder().getName().equals(podiparfolder)) {
                    this.odiFolder = folder2;
                }
            }
        }
        this.context = ((IOdiContextFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiContext.class)).findByCode(pcontextcode);
        final Collection<OdiInterface> odiInterfaces = (Collection<OdiInterface>)((IOdiInterfaceFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiInterface.class)).findByName(pname, this.project);
        return odiInterfaces;
    }
    
    public void Remove(final String pproject, final String pname) {
        OdiInterface odiInterface = null;
        final Collection<OdiInterface> odiInterfaces = (Collection<OdiInterface>)((IOdiInterfaceFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiInterface.class)).findByName(pname, pproject);
        final Iterator<OdiInterface> iterator = odiInterfaces.iterator();
        while (iterator.hasNext()) {
            final OdiInterface intf = odiInterface = iterator.next();
            System.out.print(odiInterface.getName());
        }
        if (odiInterface != null) {
            this.odiInstance.getTransactionalEntityManager().remove((IOdiEntity)odiInterface);
            System.out.println("Interface " + pname + " sucessful removed!");
        }
        else {
            System.out.println("Interface " + pname + " doesn't exists!");
        }
    }
    
    public Interface(final String purl, final String pdriver, final String pschema, final String pschemapwd, final String pworkrep, final String podiuser, final String podiuserpwd) {
        super(purl, pdriver, pschema, pschemapwd, pworkrep, podiuser, podiuserpwd);
        this.interactiveHelper = null;
    }
    
    public Interface(final String pxml) throws Exception {
        super(pxml);
        this.interactiveHelper = null;
    }
    
    private void Set_LogicalSchema(final String pstagingLogicalSchemaName) {
        this.oracleLogicalSchema = ((IOdiLogicalSchemaFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiLogicalSchema.class)).findByName(pstagingLogicalSchemaName);
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnStagingAreaSetLogicalSchema(this.oracleLogicalSchema));
    }
    
    public void AddSource(final String pname, final String pmodel, final String alias) throws Exception {
        final OdiDataStore odiDatastore1 = ((IOdiDataStoreFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiDataStore.class)).findByName(pname, pmodel);
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnSourceDataStoreSetAlias(new SourceDataStore(this.dataSet, false, alias, 0, odiDatastore1), alias));
    }
    
    public void AddTempSource(final String pname, final String pproject, final String alias) {
        final Collection<OdiInterface> tempInterfaces = (Collection<OdiInterface>)((IOdiInterfaceFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiInterface.class)).findByName(pname, pproject);
        final OdiInterface tempInterface = (OdiInterface)tempInterfaces.toArray()[0];
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnSourceDataStoreSetAlias(new SourceDataStore(this.dataSet, false, alias, 0, tempInterface), alias));
    }
    
    public void AddTarget(final String pname, final String pmodel, final String ptype, final String pstagingLogicalSchemaName) {
        if (ptype.equals("TMP")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTemporaryTargetDataStoreSetName(pname));
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTemporaryTargetDataStoreSetDatabaseSchema(OdiInterface.DatabaseSchema.TEMPORARY_SCHEMA));
            this.Set_LogicalSchema(pstagingLogicalSchemaName);
        }
        else {
            final OdiDataStore targetDatastore = ((IOdiDataStoreFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiDataStore.class)).findByName(pname, pmodel);
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionSetTargetDataStore(targetDatastore, (IMappingMatchPolicy)new MappingMatchPolicyLazy(), (IAutoMappingComputer)new AutoMappingComputerLazy(), (IAutoMappingComputer)new AutoMappingComputerLazy(), (ITargetKeyChooser)new TargetKeyChooserPrimaryKey()));
        }
    }
    
    public void AddTargetColumn(final String pname, final String ptype, final int plength, final int pprecision) {
        System.out.println(pname);
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTemporaryTargetDataStoreAddColumn(pname, (IAutoMappingComputer)new AutoMappingComputerColumnName()));
        final OdiDataType type = ((IOdiDataTypeFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiDataType.class)).findByTechnology("ORACLE", ptype);
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetDataType(pname, type));
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetLength(pname, plength));
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetScale(pname, pprecision));
    }
    
    public void AddJoin(final String ptype, final String prule, final String pexecution) {
        Join join;
        if (pexecution.toUpperCase().equals("SOURCE")) {
            join = new Join(this.dataSet, prule, false, OdiInterface.ExecutionLocation.SOURCE);
        }
        else if (pexecution.toUpperCase().equals("TARGET")) {
            join = new Join(this.dataSet, prule, false, OdiInterface.ExecutionLocation.TARGET);
        }
        else if (pexecution.toUpperCase().equals("WORK")) {
            join = new Join(this.dataSet, prule, false, OdiInterface.ExecutionLocation.WORK);
        }
        else {
            join = new Join(this.dataSet, prule, false, OdiInterface.ExecutionLocation.SOURCE);
        }
        if (ptype.toUpperCase().equals("LEFT JOIN")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnJoinSetJoinProperties(join, false, true, false));
        }
        if (ptype.toUpperCase().equals("RIGHT JOIN")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnJoinSetJoinProperties(join, false, false, true));
        }
        if (ptype.toUpperCase().equals("FULL JOIN")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnJoinSetJoinProperties(join, false, true, true));
        }
        if (ptype.toUpperCase().equals("INNER JOIN")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnJoinSetJoinProperties(join, false, false, false));
        }
    }
    
    public void Addlookup(final String ptab, final String pmodel, final String prule, final String pexecution) {
        final OdiDataStore lkpDatastore = ((IOdiDataStoreFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiDataStore.class)).findByName(ptab, pmodel);
        if (pexecution.toUpperCase().equals("SOURCE")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddLookup(this.dataSet, lkpDatastore, ptab, prule, OdiInterface.ExecutionLocation.SOURCE, false));
        }
        else if (pexecution.toUpperCase().equals("TARGET")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddLookup(this.dataSet, lkpDatastore, ptab, prule, OdiInterface.ExecutionLocation.TARGET, false));
        }
        else if (pexecution.toUpperCase().equals("WORK")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddLookup(this.dataSet, lkpDatastore, ptab, prule, OdiInterface.ExecutionLocation.WORK, false));
        }
        else {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddLookup(this.dataSet, lkpDatastore, ptab, prule, OdiInterface.ExecutionLocation.SOURCE, false));
        }
    }
    
    public void AddFilter(final String prule, final String pexecution) {
        if (pexecution.toUpperCase().equals("SOURCE")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddFilter(this.dataSet, prule, OdiInterface.ExecutionLocation.SOURCE));
        }
        else if (pexecution.toUpperCase().equals("TARGET")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddFilter(this.dataSet, prule, OdiInterface.ExecutionLocation.TARGET));
        }
        else if (pexecution.toUpperCase().equals("WORK")) {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddFilter(this.dataSet, prule, OdiInterface.ExecutionLocation.WORK));
        }
        else {
            this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionAddFilter(this.dataSet, prule, OdiInterface.ExecutionLocation.SOURCE));
        }
    }
    
    public void AddMapping(final String pcolumn, final String pexpressao, final boolean insert, final boolean update, final boolean pk, final boolean nt, final String business) {
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetMappingSetSql(pcolumn, pexpressao, this.dataSet));
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetIndicator(InterfaceActionOnTargetColumnSetIndicator.IndicatorType.UPDATE, pcolumn, update));
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetIndicator(InterfaceActionOnTargetColumnSetIndicator.IndicatorType.INSERT, pcolumn, insert));
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetIndicator(InterfaceActionOnTargetColumnSetIndicator.IndicatorType.UPDATE_KEY, pcolumn, pk));
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetIndicator(InterfaceActionOnTargetColumnSetIndicator.IndicatorType.CHECK_NOT_NULL, pcolumn, nt));
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionOnTargetColumnSetBusinessRule(pcolumn, business));
    }
    
    public void AddIkm(final String pikm) {
        final Collection<OdiIKM> odiIKM_1 = (Collection<OdiIKM>)((IOdiIKMFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiIKM.class)).findByName(pikm, this.project);
        OdiIKM odiikm = null;
        final Iterator<OdiIKM> iterator = odiIKM_1.iterator();
        while (iterator.hasNext()) {
            final OdiIKM ikm = odiikm = iterator.next();
        }
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionSetKM((OdiKM)odiikm, (IInterfaceSubComponent)this.odiInterface.getTargetDataStore(), InterfaceActionSetKM.KMType.IKM, (IKMOptionRetainer)new KMOptionRetainerLazy()));
    }
    
    public void AddCkm(final String pckm) {
        final Collection<OdiCKM> odiCKM = (Collection<OdiCKM>)((IOdiCKMFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiCKM.class)).findByName(pckm, this.project);
        OdiCKM odickm = null;
        final Iterator<OdiCKM> iterator = odiCKM.iterator();
        while (iterator.hasNext()) {
            final OdiCKM ckm = odickm = iterator.next();
        }
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionSetKM((OdiKM)odickm, (IInterfaceSubComponent)this.odiInterface.getTargetDataStore(), InterfaceActionSetKM.KMType.CKM, (IKMOptionRetainer)new KMOptionRetainerLazy()));
    }
    
    public void AddLkm(final String plkm) {
        final Collection<OdiLKM> odiLKM = (Collection<OdiLKM>)((IOdiLKMFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiLKM.class)).findByName(plkm, this.project);
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionSetKM((OdiKM)odiLKM, (IInterfaceSubComponent)this.odiInterface.getTargetDataStore(), InterfaceActionSetKM.KMType.LKM, (IKMOptionRetainer)new KMOptionRetainerLazy()));
    }
    
    public void SetIkmOpition(final String poption) {
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionSetKMOptionValue((Object)this.odiInterface.getTargetDataStore(), InterfaceActionSetKM.KMType.IKM, poption.toUpperCase(), (Object)true));
    }
    
    public void SetCkmOpition(final String poption) {
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionSetKMOptionValue((Object)this.odiInterface.getTargetDataStore(), InterfaceActionSetKM.KMType.CKM, poption.toUpperCase(), (Object)true));
    }
    
    public void SetLkmOpition(final String poption) {
        this.interactiveHelper.performAction((IInterfaceAction)new InterfaceActionSetKMOptionValue((Object)this.odiInterface.getTargetDataStore(), InterfaceActionSetKM.KMType.LKM, poption.toUpperCase(), (Object)true));
    }
    
    public void Save_interface() {
        this.interactiveHelper.computeSourceSets();
        this.interactiveHelper.preparePersist();
        this.odiInstance.getTransactionalEntityManager().persist((IOdiEntity)this.odiInterface);
    }
}