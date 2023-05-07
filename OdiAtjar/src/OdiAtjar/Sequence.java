package OdiAtjar;

import oracle.odi.domain.IOdiEntity;
import oracle.odi.domain.project.finder.IOdiSequenceFinder;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.project.OdiSequence;

public class Sequence extends Connection
{
    private OdiSequence odisequence;
    
    public Sequence(final String purl, final String pdriver, final String pschema, final String pschemapwd, final String pworkrep, final String podiuser, final String podiuserpwd) {
        super(purl, pdriver, pschema, pschemapwd, pworkrep, podiuser, podiuserpwd);
    }
    
    public Sequence(final String pxml) throws Exception {
        super(pxml);
    }
    
    public void Create(final String pproject, final String pname, final int pincrementalvalue, final String plogicalschema) {
        final String name = pname.replace("_NEXTVAL", "");
        final OdiProject odiproject = ((IOdiProjectFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiProject.class)).findByCode(pproject);
        final OdiSequence odisequencefd = ((IOdiSequenceFinder)this.odiInstance.getTransactionalEntityManager().getFinder((Class)OdiSequence.class)).findByName(name, pproject);
        if (odisequencefd == null) {
            (this.odisequence = new OdiSequence(odiproject, name, Integer.valueOf(pincrementalvalue))).setType(OdiSequence.SequenceType.NATIVE);
            this.odisequence.setLogicalSchemaName(plogicalschema);
            this.odisequence.setNativeSequenceName(name);
            this.odiInstance.getTransactionalEntityManager().persist((IOdiEntity)this.odisequence);
        }
        else {
            System.out.println("Sequence: " + name + " already exists!");
        }
    }
}