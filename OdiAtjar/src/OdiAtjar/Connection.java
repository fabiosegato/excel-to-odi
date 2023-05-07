

package OdiAtjar;

import java.util.Iterator;
import java.util.Collection;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.core.security.Authentication;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.persistence.transaction.ITransactionDefinition;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.OdiInstance;

public abstract class Connection
{
    protected OdiInstance odiInstance;
    protected String odiuser;
    protected String odiuserpwd;
    protected ITransactionManager tm;
    protected ITransactionStatus txnStatus;
    protected ITransactionDefinition txnDef;
    
    public Connection() {
    }
    
    public Connection(final String purl, final String pdriver, final String pschema, final String pschemapwd, final String pworkrep, final String podiuser, final String podiuserpwd) {
        this.odiuser = podiuser;
        this.odiuserpwd = podiuserpwd;
        final MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(purl, pdriver, pschema, pschemapwd.toCharArray(), new PoolingAttributes());
        final WorkRepositoryDbInfo workInfo = new WorkRepositoryDbInfo(pworkrep, new PoolingAttributes());
        final OdiInstanceConfig config = new OdiInstanceConfig(masterInfo, workInfo);
        this.odiInstance = OdiInstance.createInstance(config);
    }
    
    public Connection(final String pxml) throws Exception {
        final Xml xml = new Xml(pxml);
        final Decoder decoder = new Decoder();
        this.odiuser = xml.getAtributte("LoginUser");
        System.out.println("User: " + this.odiuser);
        this.odiuserpwd = decoder.decodifica(xml.getAtributte("LoginPass"));
        System.out.println("User pwd: ******");
        final String purl = xml.getAtributte("LoginDburl");
        System.out.println("Url: " + purl);
        final String pdriver = xml.getAtributte("LoginDbdriver");
        System.out.println("Driver: " + pdriver);
        final String pschema = xml.getAtributte("LoginDbuser");
        System.out.println("Master User: " + pschema);
        final String pschemapwd = decoder.decodifica(xml.getAtributte("LoginDbpass"));
        System.out.println("Master pwd: *******");
        final String pworkrep = xml.getAtributte("LoginWorkRepository");
        System.out.println("Workrep: " + pworkrep);
        final MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(purl, pdriver, pschema, pschemapwd.toCharArray(), new PoolingAttributes());
        final WorkRepositoryDbInfo workInfo = new WorkRepositoryDbInfo(pworkrep, new PoolingAttributes());
        final OdiInstanceConfig config = new OdiInstanceConfig(masterInfo, workInfo);
        this.odiInstance = OdiInstance.createInstance(config);
    }
    
    public void start_transaction() {
        final Authentication auth = this.odiInstance.getSecurityManager().createAuthentication(this.odiuser, this.odiuserpwd.toCharArray());
        this.odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);
        this.txnDef = (ITransactionDefinition)new DefaultTransactionDefinition();
        this.tm = this.odiInstance.getTransactionManager();
        this.txnStatus = this.tm.getTransaction(this.txnDef);
    }
    
    public void commit_transaction() {
        this.tm.commit(this.txnStatus);
        this.odiInstance.getSecurityManager().clearCurrentThreadAuthentication();
    }
    
    public void close() {
        this.odiInstance.close();
    }
    
    protected OdiFolder FindFolder(final String podifolder, final String podiparfolder, final String project) {
        @SuppressWarnings("rawtypes")
		final Collection<OdiFolder> odiFolders = (Collection<OdiFolder>)((IOdiFolderFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiFolder.class)).findByName(podifolder, project);
        if (podiparfolder.equals("")) {
            final Iterator<OdiFolder> iterator = odiFolders.iterator();
            if (iterator.hasNext()) {
                final OdiFolder folder1 = iterator.next();
                return folder1;
            }
        }
        else {
            for (final OdiFolder folder2 : odiFolders) {
                if (folder2.getParentFolder().getName().equals(podiparfolder)) {
                    return folder2;
                }
            }
        }
        return null;
    }
}