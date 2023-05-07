package OdiAtjar;

import java.util.List;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.Step;
import oracle.odi.domain.project.StepVariable;
import oracle.odi.domain.project.OdiVariable;
import oracle.odi.domain.project.finder.IOdiVariableFinder;
import oracle.odi.domain.project.StepInterface;
import oracle.odi.domain.project.OdiInterface;
import oracle.odi.domain.project.finder.IOdiInterfaceFinder;
import oracle.odi.domain.project.IOptionValue;
import oracle.odi.domain.project.StepProcedure;
import oracle.odi.domain.project.OdiUserProcedure;
import oracle.odi.domain.project.finder.IOdiUserProcedureFinder;
import java.util.Iterator;
import java.util.Collection;
import oracle.odi.domain.IOdiEntity;
import oracle.odi.domain.project.finder.IOdiPackageFinder;
import oracle.odi.domain.project.OdiPackage;

public class Package extends Connection
{
    private OdiPackage pack;
    
    public Package(final String pxml) throws Exception {
        super(pxml);
    }
    
    public Package() {
    }
    
    public Package(final String purl, final String pdriver, final String pschema, final String pschemapwd, final String pworkrep, final String podiuser, final String podiuserpwd) {
        super(purl, pdriver, pschema, pschemapwd, pworkrep, podiuser, podiuserpwd);
    }
    
    public void Remove(final String podifolder, final String project, final String name) {
        final Collection<OdiPackage> odiPackages = (Collection<OdiPackage>)((IOdiPackageFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiPackage.class)).findByName("PKG_" + name, project, podifolder);
        for (final OdiPackage odiPackage : odiPackages) {
            System.out.println("Removing: " + odiPackage.getName());
            this.odiInstance.getTransactionalEntityManager().remove((IOdiEntity)odiPackage);
        }
    }
    
    public OdiPackage getPackage(final String name, final String project, final String podifolder) {
        final Collection<OdiPackage> odiPackages = (Collection<OdiPackage>)((IOdiPackageFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiPackage.class)).findByName(name, project, podifolder);
        final Iterator<OdiPackage> iterator = odiPackages.iterator();
        if (iterator.hasNext()) {
            final OdiPackage odiPackage = iterator.next();
            return odiPackage;
        }
        return null;
    }
    
    public void Create(final String podifolder, final String podiparfolder, final String project, final String name, final String processo) {
        OdiUserProcedure procedure = null;
        OdiInterface interf = null;
        final OdiFolder odiFolder = this.FindFolder(podifolder, podiparfolder, project);
        this.pack = new OdiPackage(odiFolder, "PKG_" + name);
        final Collection<OdiUserProcedure> OdiUserProcedures = (Collection<OdiUserProcedure>)((IOdiUserProcedureFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiUserProcedure.class)).findByName("PRC_LOAD_BIIN_LOG_PROCESSO", project, "3 - DATA MART - INN");
        final Iterator<OdiUserProcedure> iterator = OdiUserProcedures.iterator();
        while (iterator.hasNext()) {
            final OdiUserProcedure proc = procedure = iterator.next();
        }
        final StepProcedure stepproc = new StepProcedure(this.pack, procedure, "PRC_LOAD_BIIN_LOG_PROCESSO");
        final List<IOptionValue> prcopt = (List<IOptionValue>)stepproc.getProcedureOptions();
        for (final IOptionValue opt : prcopt) {
            if (opt.getName().equals("OP_FLUXO")) {
                opt.setValue((Object)"PARAR");
            }
            else if (opt.getName().equals("OP_NM_CARGA")) {
                opt.setValue((Object)"#v_BIIN_NM_CARGA_BI");
            }
            else {
                if (!opt.getName().equals("OP_TP_CARGA")) {
                    continue;
                }
                opt.setValue((Object)"PROCESSO");
            }
        }
        final Collection<OdiInterface> OdiInterfaces = (Collection<OdiInterface>)((IOdiInterfaceFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiInterface.class)).findByName(name, project);
        final Iterator<OdiInterface> iterator3 = OdiInterfaces.iterator();
        while (iterator3.hasNext()) {
            final OdiInterface intf = interf = iterator3.next();
        }
        final StepInterface stepinterface = new StepInterface(this.pack, interf, name);
        final OdiVariable odiVariable = ((IOdiVariableFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiVariable.class)).findByName("v_BIIN_NM_CARGA_BI", project);
        final StepVariable stepvariable = new StepVariable(this.pack, odiVariable, odiVariable.getName());
        stepvariable.setAction((StepVariable.IVariableAction)new StepVariable.SetVariable(processo));
        stepvariable.setNextStepAfterSuccess((Step)stepinterface);
        stepinterface.setNextStepAfterFailure((Step)stepproc);
        stepinterface.setNextStepAfterSuccess((Step)stepproc);
        this.pack.setFirstStep((Step)stepvariable);
    }
    
    public void Save_package() {
        this.odiInstance.getTransactionalEntityManager().persist((IOdiEntity)this.pack);
    }
}