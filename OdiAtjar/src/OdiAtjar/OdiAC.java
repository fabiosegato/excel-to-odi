package OdiAtjar;

import java.io.PrintWriter;
import jxl.Sheet;
import jxl.Cell;
import jxl.Workbook;
import java.io.File;

public class OdiAC
{
    protected String Xlsfile;
    protected String LoginXml;
    protected String Url;
    protected String Driver;
    protected String MasterUser;
    protected String MasterPwd;
    protected String Workrep;
    protected String User;
    protected String Pwd;
    
    public OdiAC(final String pXlsfile, final String pUrl, final String pDriver, final String pMasterUser, final String pMasterPwd, final String pWorkrep, final String pUser, final String pPwd) {
        this.Xlsfile = "";
        this.LoginXml = "";
        this.Xlsfile = pXlsfile;
        this.Url = pUrl;
        this.Driver = pDriver;
        this.MasterUser = pMasterUser;
        this.MasterPwd = pMasterPwd;
        this.Workrep = pWorkrep;
        this.User = pUser;
        this.Pwd = pPwd;
    }
    
    public OdiAC(final String pXlsfile, final String pLoginXml) {
        this.Xlsfile = "";
        this.LoginXml = "";
        this.Xlsfile = pXlsfile;
        this.LoginXml = pLoginXml;
    }
    
    public void CreateInterface() throws Exception {
        System.out.println("Creating Interface...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        Cell jointab = null;
        Cell filter = null;
        Cell lookup = null;
        for (int sheetNumber = 0; sheetNumber < sheetNames.length; ++sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                System.out.println("Processing " + sheet.getName() + "..\n");
                final String caminhoProjeto = sheet.getCell(2, 6).getContents();
                final String[] partesCaminho = caminhoProjeto.split("/");
                final String projectcode = partesCaminho[0];
                System.out.println("Project: " + projectcode);
                final String parfoldername = partesCaminho[partesCaminho.length - 2];
                final String foldername = partesCaminho[partesCaminho.length - 1];
                System.out.println("Folder: " + foldername);
                final String iname = sheet.getCell(4, 2).getContents();
                System.out.println("Interface: " + iname);
                final String contextcode = "DEV";
                System.out.println("Context: " + contextcode);
                Interface inf;
                if (this.LoginXml.equals("")) {
                    inf = new Interface(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
                }
                else {
                    inf = new Interface(this.LoginXml);
                }
                inf.start_transaction();
                inf.Create(projectcode, foldername, parfoldername, contextcode, iname);
                for (int i = 1; i < sheet.getRows(); ++i) {
                    final Cell getval = sheet.getCell(0, i);
                    if (getval.getContents().toLowerCase().equals("source")) {
                        final Cell srcmodel = sheet.getCell(2, i);
                        final Cell srctab = sheet.getCell(3, i);
                        final Cell srcalias = sheet.getCell(4, i);
                        if (srctab.getContents().contains("TMP")) {
                            inf.AddTempSource(srctab.getContents(), projectcode, srcalias.getContents());
                            System.out.println("Add temp Source: " + srcmodel.getContents() + "->" + srctab.getContents());
                        }
                        else {
                            inf.AddSource(srctab.getContents(), srcmodel.getContents(), srcalias.getContents());
                            System.out.println("Add Source: " + srcmodel.getContents() + "->" + srctab.getContents());
                        }
                    }
                    if (getval.getContents().toLowerCase().equals("target")) {
                        final Cell tgtmodel = sheet.getCell(2, i);
                        final Cell tgttab = sheet.getCell(3, i);
                        String tipo = "";
                        if (tgttab.getContents().contains("TMP")) {
                            tipo = "TMP";
                        }
                        inf.AddTarget(tgttab.getContents(), tgtmodel.getContents(), tipo, tgtmodel.getContents());
                        System.out.println("Add Target " + tipo + " " + tgtmodel.getContents() + "->" + tgttab.getContents());
                        if (tgttab.getContents().contains("TMP")) {
                            for (int j = 1; j < sheet.getRows(); ++j) {
                                final Cell getcolumn = sheet.getCell(0, j);
                                if (getcolumn.getContents().toLowerCase().equals("mapping")) {
                                    final Cell tgtcolumn = sheet.getCell(4, j);
                                    final Cell tgttype = sheet.getCell(5, j);
                                    final Cell tgtlength = sheet.getCell(6, j);
                                    final Cell tgtscale = sheet.getCell(7, j);
                                    final int length = tgtlength.getContents().equals("") ? 0 : Integer.parseInt(tgtlength.getContents());
                                    final int scale = tgtscale.getContents().equals("") ? 0 : Integer.parseInt(tgtscale.getContents());
                                    inf.AddTargetColumn(tgtcolumn.getContents(), tgttype.getContents(), length, scale);
                                    System.out.println("Add Temp column: " + tgtcolumn.getContents());
                                }
                            }
                        }
                    }
                    lookup = sheet.getCell(1, i);
                    if (getval.getContents().toLowerCase().contains("lookup") && !lookup.getContents().equals("")) {
                        final Cell lkpmodel = sheet.getCell(1, i);
                        System.out.println(lkpmodel.getContents());
                        final Cell lkptab = sheet.getCell(2, i);
                        System.out.println(lkptab.getContents());
                        final Cell lkprule = sheet.getCell(4, i);
                        System.out.println(lkprule.getContents());
                        inf.Addlookup(lkptab.getContents(), lkpmodel.getContents(), lkprule.getContents(), "SOURCE");
                        System.out.println("Add lookup :" + lkprule.getContents());
                    }
                    jointab = sheet.getCell(2, i);
                    if (getval.getContents().toLowerCase().contains("join") && !jointab.getContents().equals("")) {
                        final Cell joinfd2 = sheet.getCell(4, i);
                        final String join_exp = joinfd2.getContents();
                        final Cell join_type = sheet.getCell(5, i);
                        inf.AddJoin(join_type.getContents(), join_exp, "SOURCE");
                        System.out.println("Add " + join_type.getContents() + ": " + join_exp);
                    }
                    if (getval.getContents().toLowerCase().equals("mapping")) {
                        final Cell col = sheet.getCell(4, i);
                        final Cell exp = sheet.getCell(16, i);
                        final Cell pk = sheet.getCell(12, i);
                        final Cell update = sheet.getCell(14, i);
                        final Cell insert = sheet.getCell(13, i);
                        final Cell nt = sheet.getCell(10, i);
                        final Cell business = sheet.getCell(3, i);
                        final boolean bpk = pk.getContents().toUpperCase().equals("SIM");
                        final boolean bupdate = update.getContents().toUpperCase().equals("SIM");
                        final boolean binsert = insert.getContents().toUpperCase().equals("SIM");
                        final boolean bnt = nt.getContents().toUpperCase().equals("SIM");
                        final String sbusiness = business.getContents();
                        System.out.println("Add Mapping: " + col.getContents() + "=" + exp.getContents());
                        inf.AddMapping(col.getContents(), exp.getContents(), binsert, bupdate, bpk, bnt, sbusiness);
                    }
                    filter = sheet.getCell(3, i);
                    if (getval.getContents().toLowerCase().equals("filter") && !filter.getContents().equals("")) {
                        final Cell filterexp = sheet.getCell(3, i);
                        final String filterrule = filterexp.getContents();
                        inf.AddFilter(filterrule, "SOURCE");
                        System.out.println("Add Filter: " + filterrule);
                    }
                    if (getval.getContents().toLowerCase().equals("km")) {
                        final Cell tpkm = sheet.getCell(1, i);
                        final Cell nmkm = sheet.getCell(2, i);
                        if (tpkm.getContents().equals("IKM")) {
                            inf.AddIkm(nmkm.getContents());
                        }
                        if (tpkm.getContents().equals("CKM")) {
                            inf.AddCkm(nmkm.getContents());
                        }
                        if (tpkm.getContents().equals("LKM")) {
                            inf.AddLkm(nmkm.getContents());
                        }
                        System.out.println("Add " + tpkm.getContents() + ": " + nmkm.getContents());
                    }
                }
                inf.Save_interface();
                inf.commit_transaction();
                inf.close();
                System.out.println("Interface " + iname.toUpperCase() + " sucessful created!");
            }
        }
        w.close();
    }
    
    public void RemoveInterface() throws Exception {
        System.out.println("Romoving Interface...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        for (int sheetNumber = sheetNames.length - 1; sheetNumber >= 0; --sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                System.out.println("Processing " + sheet.getName() + "..\n");
                final String caminhoProjeto = sheet.getCell(2, 6).getContents();
                final String[] partesCaminho = caminhoProjeto.split("/");
                final String projectcode = partesCaminho[0];
                System.out.println("Project: " + projectcode);
                final String iname = sheet.getCell(4, 2).getContents();
                System.out.println("Interface: " + iname);
                Interface inf;
                if (this.LoginXml.equals("")) {
                    inf = new Interface(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
                }
                else {
                    inf = new Interface(this.LoginXml);
                }
                inf.start_transaction();
                System.out.println("Removing Interface: " + iname.toUpperCase() + "...");
                inf.Remove(projectcode, iname);
                inf.commit_transaction();
                inf.close();
            }
        }
        w.close();
    }
    
    public void RemovePackage() throws Exception {
        System.out.println("Creating Package...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        for (int sheetNumber = sheetNames.length - 1; sheetNumber >= 0; --sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                System.out.println("Processing " + sheet.getName() + "..\n");
                final String caminhoProjeto = sheet.getCell(2, 6).getContents();
                final String[] partesCaminho = caminhoProjeto.split("/");
                final String projectcode = partesCaminho[0];
                final String foldername = partesCaminho[partesCaminho.length - 1];
                System.out.println("Project: " + projectcode);
                final String iname = sheet.getCell(4, 2).getContents();
                System.out.println("Interface: " + iname);
                Package pkg;
                if (this.LoginXml.equals("")) {
                    pkg = new Package(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
                }
                else {
                    pkg = new Package(this.LoginXml);
                }
                pkg.start_transaction();
                pkg.Remove(foldername, projectcode, iname);
                pkg.commit_transaction();
                pkg.close();
            }
        }
        w.close();
    }
    
    public void CreatePackage() throws Exception {
        System.out.println("Creating Package...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        for (int sheetNumber = sheetNames.length - 1; sheetNumber >= 0; --sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                System.out.println("Processing " + sheet.getName() + "..\n");
                final String caminhoProjeto = sheet.getCell(2, 6).getContents();
                final String[] partesCaminho = caminhoProjeto.split("/");
                final String projectcode = partesCaminho[0];
                final String parfoldername = partesCaminho[partesCaminho.length - 2];
                final String foldername = partesCaminho[partesCaminho.length - 1];
                System.out.println("Project: " + projectcode);
                final String iname = sheet.getCell(4, 2).getContents();
                System.out.println("Interface: " + iname);
                final String processo = sheet.getCell(4, 4).getContents();
                Package pkg;
                if (this.LoginXml.equals("")) {
                    pkg = new Package(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
                }
                else {
                    pkg = new Package(this.LoginXml);
                }
                pkg.start_transaction();
                System.out.println("Creating Package: PKG_" + iname.toUpperCase());
                pkg.Create(foldername, parfoldername, projectcode, iname, processo);
                pkg.commit_transaction();
                pkg.close();
            }
        }
        w.close();
    }
    
    public void CreateModel() throws Exception {
        System.out.println("Creating Model...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        String target = "";
        for (int sheetNumber = 0; sheetNumber < sheetNames.length; ++sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                final String iname = sheet.getCell(4, 2).getContents();
                if (!iname.toUpperCase().contains("TMP")) {
                    System.out.println("Processing " + sheet.getName() + "..\n");
                    Model mod;
                    if (this.LoginXml.equals("")) {
                        mod = new Model(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
                    }
                    else {
                        mod = new Model(this.LoginXml);
                    }
                    mod.start_transaction();
                    for (int i = 1; i < sheet.getRows(); ++i) {
                        final Cell getval = sheet.getCell(0, i);
                        if (getval.getContents().toLowerCase().equals("target")) {
                            final Cell tgtmodel = sheet.getCell(2, i);
                            final Cell tgttab = sheet.getCell(3, i);
                            final Cell tgtscd = sheet.getCell(4, i);
                            final String targetDs = tgtmodel.getContents().toUpperCase();
                            target = tgttab.getContents().toUpperCase();
                            final String targetscd = tgtscd.getContents().toUpperCase();
                            mod.Create(targetDs, target, targetscd);
                        }
                        if (getval.getContents().toLowerCase().equals("mapping")) {
                            final Cell columnname = sheet.getCell(4, i);
                            final Cell columntype = sheet.getCell(5, i);
                            final Cell columnlength = sheet.getCell(6, i);
                            final Cell columnscale = sheet.getCell(7, i);
                            final Cell columnpk = sheet.getCell(8, i);
                            final Cell columnnk = sheet.getCell(10, i);
                            final Cell columnscd = sheet.getCell(15, i);
                            final Cell columndesc = sheet.getCell(2, i);
                            final String clname = columnname.getContents().toUpperCase();
                            final String cdesc = columndesc.getContents();
                            final String cltype = columntype.getContents().toUpperCase();
                            final int cllength = columnlength.getContents().equals("") ? 0 : Integer.parseInt(columnlength.getContents());
                            final int clscale = columnscale.getContents().equals("") ? 0 : Integer.parseInt(columnscale.getContents());
                            final boolean clpk = columnpk.getContents().toUpperCase().equals("SIM");
                            final boolean clnk = columnnk.getContents().toUpperCase().equals("SIM");
                            final String clscd = columnscd.getContents().toUpperCase();
                            mod.AddModelColumn(clname, cltype, clnk, cllength, clscale, clpk, clscd, cdesc);
                        }
                    }
                    mod.Save_model();
                    mod.commit_transaction();
                    mod.close();
                }
            }
        }
        w.close();
    }
    
    public void CreateDDL() throws Exception {
        System.out.println("Creating DDL...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        String target = "";
        PrintWriter writer = null;
        final String[] primarykeys = new String[50];
        final String[] coments = new String[50];
        int contadorpk = 0;
        int contadorlinhas = 0;
        for (int sheetNumber = 0; sheetNumber < sheetNames.length; ++sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                final String iname = sheet.getCell(4, 2).getContents();
                if (!iname.toUpperCase().contains("TMP")) {
                    System.out.println("Processing " + sheet.getName() + "..\n");
                    for (int i = 0; i < sheet.getRows(); ++i) {
                        final Cell getval = sheet.getCell(0, i);
                        if (getval.getContents().toLowerCase().equals("target")) {
                            final Cell tgttab = sheet.getCell(3, i);
                            target = tgttab.getContents().toUpperCase();
                            System.out.println(String.valueOf(target) + ".sql");
                            writer = new PrintWriter(String.valueOf(target) + ".sql", "UTF-8");
                            writer.println("CREATE TABLE " + target);
                            writer.println("(");
                        }
                        if (getval.getContents().toLowerCase().equals("mapping")) {
                            final Cell columnname = sheet.getCell(4, i);
                            final Cell columntype = sheet.getCell(5, i);
                            final Cell columnlength = sheet.getCell(6, i);
                            final Cell columnscale = sheet.getCell(7, i);
                            final Cell columnpk = sheet.getCell(8, i);
                            final Cell columnnk = sheet.getCell(10, i);
                            final Cell columndesc = sheet.getCell(2, i);
                            final String clname = columnname.getContents().toUpperCase();
                            final String cdesc = columndesc.getContents();
                            final String cltype = columntype.getContents().toUpperCase();
                            final String cllength = columnlength.getContents();
                            final String clscale = columnscale.getContents();
                            final String clpk = columnpk.getContents().toUpperCase();
                            final String clnk = columnnk.getContents().toUpperCase().equals("SIM") ? " NOT NULL" : "";
                            final String inicio = (contadorlinhas > 0) ? "," : "";
                            if (cltype.equals("VARCHAR2") || cltype.equals("CHAR")) {
                                writer.println(String.valueOf(inicio) + clname + " " + cltype + "(" + cllength + ")" + clnk);
                            }
                            else if (cltype.equals("NUMBER")) {
                                final String escala = clscale.equals("") ? "" : ("," + clscale);
                                writer.println(String.valueOf(inicio) + clname + " " + cltype + "(" + cllength + escala + ")" + clnk);
                            }
                            else if (cltype.equals("DATE")) {
                                writer.println(String.valueOf(inicio) + clname + " " + cltype + clnk);
                            }
                            coments[contadorlinhas] = "comment on column " + target + "." + clname + " is " + "'" + cdesc + "';";
                            if (clpk.equals("SIM")) {
                                primarykeys[contadorpk] = clname;
                                ++contadorpk;
                            }
                            ++contadorlinhas;
                        }
                    }
                    String primarycolumns = "";
                    for (int j = 0; j < contadorpk; ++j) {
                        final String inicio2 = (j > 0) ? "," : "";
                        primarycolumns = String.valueOf(primarycolumns) + inicio2 + primarykeys[j];
                    }
                    if (contadorpk > 0) {
                        writer.println(", CONSTRAINT PK_" + target + " PRIMARY KEY (" + primarycolumns + ")");
                    }
                    writer.println(");");
                    for (int j = 0; j < contadorlinhas; ++j) {
                        writer.println(coments[j]);
                    }
                    writer.close();
                }
            }
        }
        w.close();
    }
    
    public void RemovePk() throws Exception {
        System.out.println("Removing Pimary Key...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        for (int sheetNumber = 0; sheetNumber < sheetNames.length; ++sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                System.out.println("Processing " + sheet.getName() + "..\n");
                Model mod;
                if (this.LoginXml.equals("")) {
                    mod = new Model(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
                }
                else {
                    mod = new Model(this.LoginXml);
                }
                mod.start_transaction();
                for (int i = 1; i < sheet.getRows(); ++i) {
                    final Cell getval = sheet.getCell(0, i);
                    if (getval.getContents().toLowerCase().equals("target")) {
                        final Cell tgtmodel = sheet.getCell(2, i);
                        final Cell tgttab = sheet.getCell(4, i);
                        final String targetDs = tgtmodel.getContents().toUpperCase();
                        final String target = tgttab.getContents().toUpperCase();
                        mod.RemovePK(target, targetDs);
                    }
                }
                mod.commit_transaction();
                mod.close();
            }
        }
        w.close();
    }
    
    public void CreateSequence() throws Exception {
        System.out.println("Creating Sequence...");
        final File inputWorkbook = new File(this.Xlsfile);
        final Workbook w = Workbook.getWorkbook(inputWorkbook);
        final String[] sheetNames = w.getSheetNames();
        for (int sheetNumber = 0; sheetNumber < sheetNames.length; ++sheetNumber) {
            final Sheet sheet = w.getSheet(sheetNames[sheetNumber]);
            if (!sheet.getName().equals("Ordem") || !sheet.getName().equals("Aux")) {
                System.out.println("Processing " + sheet.getName() + "..\n");
                final String caminhoProjeto = sheet.getCell(2, 6).getContents();
                final String[] partesCaminho = caminhoProjeto.split("/");
                final String projectcode = partesCaminho[0];
                Sequence seq;
                if (this.LoginXml.equals("")) {
                    seq = new Sequence(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
                }
                else {
                    seq = new Sequence(this.LoginXml);
                }
                seq.start_transaction();
                for (int i = 1; i < sheet.getRows(); ++i) {
                    final Cell getval = sheet.getCell(0, i);
                    final Cell exp = sheet.getCell(16, i);
                    final Cell lschema = sheet.getCell(17, i);
                    if (getval.getContents().toLowerCase().equals("mapping") && exp.getContents().toUpperCase().contains(":SQ_")) {
                        final String sqname = exp.getContents().toUpperCase();
                        final String lgname = lschema.getContents().toUpperCase();
                        System.out.println("Creating Sequence: " + sqname.replace(":", ""));
                        seq.Create(projectcode, sqname.replace(":", ""), 1, lgname);
                    }
                }
                seq.commit_transaction();
                seq.close();
            }
        }
        w.close();
    }
}