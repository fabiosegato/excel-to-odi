package OdiAtjar;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import oracle.odi.domain.project.interfaces.SourceSet;
import oracle.odi.domain.project.interfaces.Filter;
import oracle.odi.domain.model.OdiColumn;
import oracle.odi.domain.project.interfaces.TargetMapping;
import oracle.odi.domain.project.interfaces.Join;
import jxl.write.WritableCell;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.Workbook;
import java.io.File;
import oracle.odi.domain.project.OdiInterface;
import jxl.write.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Border;
import jxl.biff.FontRecord;
import jxl.write.WritableCellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableFont;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import oracle.odi.domain.project.interfaces.SourceDataStore;
import oracle.odi.domain.project.interfaces.SourceColumn;
import oracle.odi.domain.project.interfaces.DataSet;

public class OdiRD extends OdiAC
{
    public OdiRD(final String pXlsfile, final String pLoginXml) {
        super(pXlsfile, pLoginXml);
    }
    
    private SourceColumn findSource(final String expression, final DataSet dataset) {
        final Collection<SourceDataStore> sourceDataStores = (Collection<SourceDataStore>)dataset.getSourceDataStores();
        for (final SourceDataStore datastore : sourceDataStores) {
            final List<SourceColumn> columns = (List<SourceColumn>)datastore.getSourceColumns();
            for (final SourceColumn column : columns) {
                if (expression.contains(column.getName()) && expression.contains(column.getTable().getAlias())) {
                    return column;
                }
            }
        }
        return null;
    }
    
    public OdiRD(final String pXlsfile, final String pUrl, final String pDriver, final String pMasterUser, final String pMasterPwd, final String pWorkrep, final String pUser, final String pPwd) {
        super(pXlsfile, pUrl, pDriver, pMasterUser, pMasterPwd, pWorkrep, pUser, pPwd);
    }
    
    public void GenMapping(final String pintfile) throws Exception {
        final LinkedHashSet<String> listasource = new LinkedHashSet<String>();
        Interface inf;
        if (this.LoginXml.equals("")) {
            inf = new Interface(this.Url, this.Driver, this.MasterUser, this.MasterPwd, this.Workrep, this.User, this.Pwd);
        }
        else {
            inf = new Interface(this.LoginXml);
        }
        inf.start_transaction();
        final BufferedReader buffRead = new BufferedReader(new FileReader(pintfile));
        final LineNumberReader lnr = new LineNumberReader(buffRead);
        String linha = null;
        while ((linha = lnr.readLine()) != null) {
            final String[] sint = linha.split("/");
            System.out.println(linha);
            final Collection<OdiInterface> interfaces = inf.getInterface(sint[0], sint[2], sint[1], "DEV", sint[3]);
            final WritableFont TableFormatype = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
            final WritableCellFormat cellformattype = new WritableCellFormat();
            cellformattype.setFont((FontRecord)TableFormatype);
            cellformattype.setBackground(Colour.WHITE);
            cellformattype.setBorder(Border.ALL, BorderLineStyle.NONE);
            final WritableCellFormat cellformatdefault = new WritableCellFormat();
            cellformatdefault.setBackground(Colour.WHITE);
            cellformatdefault.setBorder(Border.ALL, BorderLineStyle.NONE);
            final WritableFont TableFormatheader = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
            final WritableCellFormat cellformat = new WritableCellFormat();
            cellformat.setBackground(Colour.GREY_50_PERCENT);
            cellformat.setAlignment(Alignment.CENTRE);
            cellformat.setFont((FontRecord)TableFormatheader);
            cellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
            final WritableFont TableFormatbody = new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            final WritableCellFormat cellformatbody = new WritableCellFormat();
            cellformatbody.setAlignment(Alignment.LEFT);
            cellformatbody.setFont((FontRecord)TableFormatbody);
            cellformatbody.setBorder(Border.ALL, BorderLineStyle.THIN);
            for (final OdiInterface inft : interfaces) {
                final File inputWorkbook = new File("MAP_" + inft.getName() + "_V001.xls");
                final WritableWorkbook w = Workbook.createWorkbook(inputWorkbook);
                System.out.println("Interface: " + inft.getName());
                final List<DataSet> datasets = (List<DataSet>)inft.getDataSets();
                for (final DataSet dataset : datasets) {
                    int i = 1;
                    System.out.println("Sheet: " + dataset.getName());
                    final WritableSheet sheet = w.createSheet(dataset.getName().replace("/", "_"), 0);
                    for (int c = 0; c <= 50; ++c) {
                        for (int l = 0; l <= 200; ++l) {
                            final Label labeldefult = new Label(c, l, "");
                            labeldefult.setCellFormat((CellFormat)cellformatdefault);
                            sheet.addCell((WritableCell)labeldefult);
                        }
                    }
                    final Label labelnome = new Label(1, i, "Nome");
                    final Label labeldsc0 = new Label(2, i, "Descri\u00e7\u00e3o");
                    final Label labeltbf = new Label(3, i, "Tabela Fisica(Destino)");
                    final Label labelnomeint = new Label(4, i, "Nome Interface ODI");
                    labelnomeint.setCellFormat((CellFormat)cellformat);
                    labelnome.setCellFormat((CellFormat)cellformat);
                    labeldsc0.setCellFormat((CellFormat)cellformat);
                    labeltbf.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelnomeint);
                    sheet.addCell((WritableCell)labelnome);
                    sheet.addCell((WritableCell)labeldsc0);
                    sheet.addCell((WritableCell)labeltbf);
                    ++i;
                    final Label labelnome2 = new Label(1, i, "");
                    final Label labeldsc2 = new Label(2, i, "");
                    final Label labeltbf2 = new Label(3, i, inft.getTargetDataStore().getName());
                    final Label labelnomeint2 = new Label(4, i, inft.getName());
                    labelnome2.setCellFormat((CellFormat)cellformatbody);
                    labelnomeint2.setCellFormat((CellFormat)cellformatbody);
                    labeldsc2.setCellFormat((CellFormat)cellformatbody);
                    labeltbf2.setCellFormat((CellFormat)cellformatbody);
                    sheet.addCell((WritableCell)labelnomeint2);
                    sheet.addCell((WritableCell)labelnome2);
                    sheet.addCell((WritableCell)labeldsc2);
                    sheet.addCell((WritableCell)labeltbf2);
                    ++i;
                    final Label labeldscint = new Label(1, i, "Descri\u00e7\u00e3o Interface");
                    final Label labelperiodicidade = new Label(2, i, "Periodicidade");
                    final Label labeltipocarga = new Label(3, i, "Tipo de Carga");
                    final Label labeltorre = new Label(4, i, "Torre");
                    labeldscint.setCellFormat((CellFormat)cellformat);
                    labelperiodicidade.setCellFormat((CellFormat)cellformat);
                    labeltipocarga.setCellFormat((CellFormat)cellformat);
                    labeltorre.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeldscint);
                    sheet.addCell((WritableCell)labelperiodicidade);
                    sheet.addCell((WritableCell)labeltipocarga);
                    sheet.addCell((WritableCell)labeltorre);
                    ++i;
                    final Label labeldscint2 = new Label(1, i, "");
                    final Label labelperiodicidade2 = new Label(2, i, "");
                    final Label labeltipocarga2 = new Label(3, i, "");
                    final Label labeltorre2 = new Label(4, i, "");
                    labeldscint2.setCellFormat((CellFormat)cellformatbody);
                    labelperiodicidade2.setCellFormat((CellFormat)cellformatbody);
                    labeltipocarga2.setCellFormat((CellFormat)cellformatbody);
                    labeltorre2.setCellFormat((CellFormat)cellformatbody);
                    sheet.addCell((WritableCell)labeldscint2);
                    sheet.addCell((WritableCell)labelperiodicidade2);
                    sheet.addCell((WritableCell)labeltipocarga2);
                    sheet.addCell((WritableCell)labeltorre2);
                    ++i;
                    final Label labeldemanda = new Label(1, i, "Demanda");
                    final Label labelprojeto = new Label(2, i, "Projeto ODI");
                    final Label labelgranularidade = new Label(3, i, "Granularidade");
                    final Label labelarearesp = new Label(4, i, "\u00c1rea Respons\u00e1vel");
                    labeldemanda.setCellFormat((CellFormat)cellformat);
                    labelprojeto.setCellFormat((CellFormat)cellformat);
                    labelgranularidade.setCellFormat((CellFormat)cellformat);
                    labelarearesp.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeldemanda);
                    sheet.addCell((WritableCell)labelprojeto);
                    sheet.addCell((WritableCell)labelgranularidade);
                    sheet.addCell((WritableCell)labelarearesp);
                    ++i;
                    final Label labeldemanda2 = new Label(1, i, "");
                    final Label labelprojeto2 = new Label(2, i, String.valueOf(sint[0]) + "/" + sint[1] + "/" + sint[2]);
                    final Label labelgranularidade2 = new Label(3, i, "");
                    final Label labelarearesp2 = new Label(4, i, "");
                    labeldemanda2.setCellFormat((CellFormat)cellformatbody);
                    labelprojeto2.setCellFormat((CellFormat)cellformatbody);
                    labelgranularidade2.setCellFormat((CellFormat)cellformatbody);
                    labelarearesp2.setCellFormat((CellFormat)cellformatbody);
                    sheet.addCell((WritableCell)labeldemanda2);
                    sheet.addCell((WritableCell)labelprojeto2);
                    sheet.addCell((WritableCell)labelgranularidade2);
                    sheet.addCell((WritableCell)labelarearesp2);
                    ++i;
                    final Label labelversao = new Label(1, i, "Vers\u00e3o Documento");
                    final Label labeldev = new Label(2, i, "Desenvolvedor");
                    labelversao.setCellFormat((CellFormat)cellformat);
                    labeldev.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelversao);
                    sheet.addCell((WritableCell)labeldev);
                    ++i;
                    final Label labelversao2 = new Label(1, i, "");
                    final Label labeldev2 = new Label(2, i, "");
                    labelversao2.setCellFormat((CellFormat)cellformatbody);
                    labeldev2.setCellFormat((CellFormat)cellformatbody);
                    sheet.addCell((WritableCell)labelversao2);
                    sheet.addCell((WritableCell)labeldev2);
                    ++i;
                    ++i;
                    final Collection<SourceDataStore> sources = (Collection<SourceDataStore>)dataset.getSourceDataStores();
                    final Label labelheadersource0 = new Label(1, i, "Origem");
                    labelheadersource0.setCellFormat((CellFormat)cellformat);
                    final Label labelheadersource2 = new Label(2, i, "Modelo/Schema");
                    labelheadersource2.setCellFormat((CellFormat)cellformat);
                    final Label labelheadersource3 = new Label(3, i, "Tabela/Interface");
                    labelheadersource3.setCellFormat((CellFormat)cellformat);
                    final Label labelheaderalias = new Label(4, i, "Alias");
                    labelheaderalias.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelheadersource0);
                    sheet.addCell((WritableCell)labelheadersource2);
                    sheet.addCell((WritableCell)labelheadersource3);
                    sheet.addCell((WritableCell)labelheaderalias);
                    ++i;
                    for (final SourceDataStore source : sources) {
                        if (source.getName().toUpperCase().contains("DW")) {
                            listasource.add(source.getName());
                        }
                        final String pmodel = (source.getUnderlyingOdiDataStore() == null) ? source.getLogicalSchema().getName() : source.getUnderlyingOdiDataStore().getModel().getName();
                        final String porigem = (source.getUnderlyingOdiDataStore() != null) ? source.getUnderlyingOdiDataStore().getDataStoreType().toString() : "Interface";
                        final Label labelsourcetp = new Label(0, i, "Source");
                        labelsourcetp.setCellFormat((CellFormat)cellformattype);
                        final Label labelsourceorig = new Label(1, i, porigem);
                        labelsourceorig.setCellFormat((CellFormat)cellformatbody);
                        final Label labelsourcem = new Label(2, i, pmodel);
                        labelsourcem.setCellFormat((CellFormat)cellformatbody);
                        final Label labelsourcet = new Label(3, i, source.getName());
                        labelsourcet.setCellFormat((CellFormat)cellformatbody);
                        final Label labelsourcealias = new Label(4, i, source.getAlias());
                        labelsourcealias.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelsourcealias);
                        sheet.addCell((WritableCell)labelsourcem);
                        sheet.addCell((WritableCell)labelsourcet);
                        sheet.addCell((WritableCell)labelsourcetp);
                        sheet.addCell((WritableCell)labelsourceorig);
                        ++i;
                    }
                    ++i;
                    final Label labelheadertgt0 = new Label(1, i, "Destino");
                    labelheadertgt0.setCellFormat((CellFormat)cellformat);
                    final Label labelheadertgt2 = new Label(2, i, "Modelo/Schema");
                    labelheadertgt2.setCellFormat((CellFormat)cellformat);
                    final Label labelheadertgt3 = new Label(3, i, "Tabela/Interface");
                    labelheadertgt3.setCellFormat((CellFormat)cellformat);
                    final Label labelheadertgt4 = new Label(4, i, "SCD");
                    labelheadertgt4.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelheadertgt2);
                    sheet.addCell((WritableCell)labelheadertgt3);
                    ++i;
                    final String pmodel2 = (inft.getTargetDataStore().getUnderlyingOdiDataStore() == null) ? inft.getTargetDataStore().getLogicalSchema().getName() : inft.getTargetDataStore().getUnderlyingOdiDataStore().getModel().getName();
                    final String porigem2 = (inft.getTargetDataStore().getUnderlyingOdiDataStore() == null) ? "Interface" : inft.getTargetDataStore().getUnderlyingOdiDataStore().getDataStoreType().toString();
                    String pscd;
                    if (inft.getTargetDataStore().isTemporaryDataStore()) {
                        pscd = "N\u00e3o";
                    }
                    else {
                        pscd = ((inft.getTargetDataStore().getUnderlyingOdiDataStore().getOlapType() != null) ? "Sim" : "N\u00e3o");
                    }
                    final Label labeltargetp = new Label(0, i, "Target");
                    labeltargetp.setCellFormat((CellFormat)cellformattype);
                    final Label labeltgtorig = new Label(1, i, porigem2);
                    labeltgtorig.setCellFormat((CellFormat)cellformatbody);
                    final Label labeltargetm = new Label(2, i, pmodel2);
                    labeltargetm.setCellFormat((CellFormat)cellformatbody);
                    final Label labeltargett = new Label(3, i, inft.getTargetDataStore().getName());
                    labeltargett.setCellFormat((CellFormat)cellformatbody);
                    final Label labeltargetscd = new Label(4, i, pscd);
                    labeltargetscd.setCellFormat((CellFormat)cellformatbody);
                    sheet.addCell((WritableCell)labeltgtorig);
                    sheet.addCell((WritableCell)labeltargetm);
                    sheet.addCell((WritableCell)labeltargett);
                    sheet.addCell((WritableCell)labeltargetp);
                    sheet.addCell((WritableCell)labelheadertgt0);
                    sheet.addCell((WritableCell)labelheadertgt4);
                    sheet.addCell((WritableCell)labeltargetscd);
                    i += 2;
                    final Label labelheaderjoin0 = new Label(1, i, "Jun\u00e7\u00f5es");
                    ++i;
                    final Label labelheaderjoin2 = new Label(1, i, "Jun\u00e7\u00e3o ID (Ordem)");
                    labelheaderjoin2.setCellFormat((CellFormat)cellformat);
                    final Label labelheaderjoin3 = new Label(2, i, "Tabela1");
                    labelheaderjoin3.setCellFormat((CellFormat)cellformat);
                    final Label labelheaderjoin4 = new Label(3, i, "Tabela2");
                    labelheaderjoin4.setCellFormat((CellFormat)cellformat);
                    final Label labelheaderjoin5 = new Label(4, i, "Express\u00e3o");
                    labelheaderjoin5.setCellFormat((CellFormat)cellformat);
                    final Label labelheaderjoin6 = new Label(5, i, "Tipo de Join");
                    labelheaderjoin6.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelheaderjoin0);
                    sheet.addCell((WritableCell)labelheaderjoin2);
                    sheet.addCell((WritableCell)labelheaderjoin3);
                    sheet.addCell((WritableCell)labelheaderjoin4);
                    sheet.addCell((WritableCell)labelheaderjoin5);
                    sheet.addCell((WritableCell)labelheaderjoin6);
                    ++i;
                    final Collection<Join> joins = (Collection<Join>)dataset.getJoins();
                    for (final Join join : joins) {
                        String jointype = null;
                        if (join.isOuter1()) {
                            jointype = "left join";
                        }
                        else if (join.isOuter2()) {
                            jointype = "rigth join";
                        }
                        else if (!join.isOuter2() && !join.isOuter1()) {
                            jointype = "inner join";
                        }
                        final String rigths = join.getAttachedDataStore2().getName();
                        final String lefts = join.getAttachedDataStore1().getName();
                        final String joinexp = join.getSqlExpression().toString();
                        final int joinorder = join.getClauseOrder();
                        final Label labeljoitp = new Label(0, i, "Join");
                        labeljoitp.setCellFormat((CellFormat)cellformattype);
                        final Label labeljoiorder = new Label(1, i, Integer.toString(joinorder));
                        labeljoiorder.setCellFormat((CellFormat)cellformatbody);
                        final Label labeljoinl = new Label(2, i, lefts);
                        labeljoinl.setCellFormat((CellFormat)cellformatbody);
                        final Label labeljoicp1 = new Label(3, i, rigths);
                        labeljoicp1.setCellFormat((CellFormat)cellformatbody);
                        final Label labeljoinr = new Label(4, i, joinexp);
                        labeljoinr.setCellFormat((CellFormat)cellformatbody);
                        final Label labeljointype = new Label(5, i, jointype);
                        labeljointype.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeljoinr);
                        sheet.addCell((WritableCell)labeljoinl);
                        sheet.addCell((WritableCell)labeljoicp1);
                        sheet.addCell((WritableCell)labeljoitp);
                        sheet.addCell((WritableCell)labeljointype);
                        sheet.addCell((WritableCell)labeljoiorder);
                        ++i;
                    }
                    ++i;
                    final Label labelmapping = new Label(1, i, "Mapeamento");
                    sheet.addCell((WritableCell)labelmapping);
                    ++i;
                    final Label labeldestino = new Label(1, i, "Destino");
                    labeldestino.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeldestino);
                    final Label labelbranco = new Label(2, i, "");
                    labelbranco.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco);
                    final Label labelbranco2 = new Label(3, i, "");
                    labelbranco2.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco2);
                    final Label labelbranco3 = new Label(4, i, "");
                    labelbranco3.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco3);
                    final Label labelbranco4 = new Label(5, i, "");
                    labelbranco4.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco4);
                    final Label labelbranco5 = new Label(6, i, "");
                    labelbranco5.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco5);
                    final Label labelbranco6 = new Label(7, i, "");
                    labelbranco6.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco6);
                    final Label labelbranco7 = new Label(8, i, "");
                    labelbranco7.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco7);
                    final Label labelbranco8 = new Label(9, i, "");
                    labelbranco8.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco8);
                    final Label labelbranco9 = new Label(10, i, "");
                    labelbranco9.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco9);
                    final Label labelbranco10 = new Label(11, i, "");
                    labelbranco10.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco10);
                    final Label labelbranco11 = new Label(12, i, "");
                    labelbranco11.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco11);
                    final Label labelbranco12 = new Label(13, i, "");
                    labelbranco12.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco12);
                    final Label labelbranco13 = new Label(14, i, "");
                    labelbranco13.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco13);
                    final Label labelbranco14 = new Label(15, i, "");
                    labelbranco14.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco14);
                    final Label labelbranco15 = new Label(16, i, "");
                    labelbranco15.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco15);
                    final Label labelbranco16 = new Label(17, i, "Origem");
                    labelbranco16.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco16);
                    final Label labelbranco17 = new Label(18, i, "");
                    labelbranco17.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco17);
                    final Label labelbranco18 = new Label(19, i, "");
                    labelbranco18.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco18);
                    final Label labelbranco19 = new Label(20, i, "");
                    labelbranco19.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco19);
                    final Label labelbranco20 = new Label(21, i, "");
                    labelbranco20.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelbranco20);
                    ++i;
                    final Label labelnl = new Label(1, i, "Nome L\u00f3gico do Campo");
                    labelnl.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelnl);
                    final Label labeldc = new Label(2, i, "Descri\u00e7\u00e3o do Campo");
                    labeldc.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeldc);
                    final Label labeldf = new Label(3, i, "Defini\u00e7\u00e3o Funcional");
                    labeldf.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeldf);
                    final Label labelfc = new Label(4, i, "Nome F\u00edsico do Campo");
                    labelfc.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelfc);
                    final Label labeltd = new Label(5, i, "Tipo de Dado");
                    labeltd.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeltd);
                    final Label labelt = new Label(6, i, "Tamanho");
                    labelt.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelt);
                    final Label labele = new Label(7, i, "Escala");
                    labele.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labele);
                    final Label labelpkk = new Label(8, i, "PK");
                    labelpkk.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelpkk);
                    final Label labelfk = new Label(9, i, "FK");
                    labelfk.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelfk);
                    final Label labelob = new Label(10, i, "Obrigat\u00f3rio");
                    labelob.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelob);
                    final Label labelpt = new Label(11, i, "Parti\u00e7\u00e3o");
                    labelpt.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelpt);
                    final Label labelchave = new Label(12, i, "Chave");
                    labelchave.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelchave);
                    final Label labelinserir = new Label(13, i, "Inserir");
                    labelinserir.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelinserir);
                    final Label labelat = new Label(14, i, "Atualizar");
                    labelat.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelat);
                    final Label labelscd = new Label(15, i, "SCD");
                    labelscd.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelscd);
                    final Label labelrg = new Label(16, i, "Regras");
                    labelrg.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelrg);
                    final Label labelarq = new Label(17, i, "Nome Arquivo/Schema");
                    labelarq.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelarq);
                    final Label labelplan = new Label(18, i, "Planilha/Tabela");
                    labelplan.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelplan);
                    final Label labelcampo = new Label(19, i, "Campo");
                    labelcampo.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labelcampo);
                    final Label labeltpd = new Label(20, i, "Tipo de Dados");
                    labeltpd.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeltpd);
                    final Label labeltdd = new Label(21, i, "Tamanho");
                    labeltdd.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labeltdd);
                    ++i;
                    final Collection<TargetMapping> mappings1 = (Collection<TargetMapping>)dataset.getMappings();
                    for (final TargetMapping mapping : mappings1) {
                        final Label labeltargemtp1 = new Label(0, i, "Mapping");
                        labeltargemtp1.setCellFormat((CellFormat)cellformattype);
                        final Label labelnlc1 = new Label(1, i, mapping.getTargetColumn().getName());
                        final Label labeldsc3 = new Label(3, i, mapping.getTargetColumn().getBusinessRule());
                        final Label labeldscf1 = new Label(2, i, (mapping.getTargetColumn().getUnderlyingOdiColumn() == null) ? "" : mapping.getTargetColumn().getUnderlyingOdiColumn().getDescription());
                        final Label labelcolumn1 = new Label(4, i, mapping.getTargetColumn().getName());
                        final Label labeldatatype1 = new Label(5, i, mapping.getTargetColumn().getDataType().getName());
                        final Label labellength1 = new Label(6, i, (mapping.getTargetColumn().getLength() == null) ? "0" : mapping.getTargetColumn().getLength().toString());
                        final Label labelscale1 = new Label(7, i, (mapping.getTargetColumn().getScale() == null) ? "0" : mapping.getTargetColumn().getScale().toString());
                        final Label labelpk1 = new Label(8, i, mapping.getTargetColumn().isUpdateKeyIndicator() ? "Sim" : "N\u00e3o");
                        final Label labelfkk1 = new Label(9, i, "");
                        final Label labelnt1 = new Label(10, i, mapping.getTargetColumn().isCheckNotNullIndicator() ? "Sim" : "N\u00e3o");
                        final Label labelpart1 = new Label(11, i, "");
                        final Label labelpk2 = new Label(12, i, mapping.getTargetColumn().isUpdateKeyIndicator() ? "Sim" : "N\u00e3o");
                        final Label labelinsert1 = new Label(13, i, mapping.getTargetColumn().isInsertIndicator() ? "Sim" : "N\u00e3o");
                        final Label labelupdate1 = new Label(14, i, mapping.getTargetColumn().isUpdateIndicator() ? "Sim" : "N\u00e3o");
                        String typescd1 = "";
                        if (mapping.getTargetColumn().getUnderlyingOdiColumn() != null) {
                            if (mapping.getTargetColumn().getUnderlyingOdiColumn().getScdType() != null) {
                                final OdiColumn.ScdType type1 = mapping.getTargetColumn().getUnderlyingOdiColumn().getScdType();
                                if (type1.equals((Object)OdiColumn.ScdType.ADD_ROW_ON_CHANGE)) {
                                    typescd1 = "ADD";
                                }
                                else if (type1.equals((Object)OdiColumn.ScdType.OVERWRITE_ON_CHANGE)) {
                                    typescd1 = "UPDATE";
                                }
                                else if (type1.equals((Object)OdiColumn.ScdType.CURRENT_RECORD_FLAG)) {
                                    typescd1 = "FLAG";
                                }
                                else if (type1.equals((Object)OdiColumn.ScdType.START_TIMESTAMP)) {
                                    typescd1 = "END";
                                }
                                else if (type1.equals((Object)OdiColumn.ScdType.START_TIMESTAMP)) {
                                    typescd1 = "START";
                                }
                                else if (type1.equals((Object)OdiColumn.ScdType.SURROGATE_KEY)) {
                                    typescd1 = "SK";
                                }
                                else if (type1.equals((Object)OdiColumn.ScdType.NATURAL_KEY)) {
                                    typescd1 = "NK";
                                }
                            }
                        }
                        else {
                            typescd1 = "";
                        }
                        final Label labelscdd1 = new Label(15, i, typescd1);
                        final Label labelexpm1 = new Label(16, i, mapping.getSqlExpression().toString());
                        Label labeorgbranco11;
                        if (this.findSource(mapping.getSqlExpression().toString(), dataset) == null) {
                            labeorgbranco11 = new Label(17, i, "");
                        }
                        else if (this.findSource(mapping.getSqlExpression().toString(), dataset).getTable().getUnderlyingOdiDataStore() != null) {
                            labeorgbranco11 = new Label(17, i, this.findSource(mapping.getSqlExpression().toString(), dataset).getTable().getUnderlyingOdiDataStore().getModel().getName());
                        }
                        else {
                            labeorgbranco11 = new Label(17, i, "");
                        }
                        final Label labeorgbranco12 = new Label(18, i, (this.findSource(mapping.getSqlExpression().toString(), dataset) == null) ? "" : this.findSource(mapping.getSqlExpression().toString(), dataset).getTable().getName());
                        final Label labeorgbranco13 = new Label(19, i, (this.findSource(mapping.getSqlExpression().toString(), dataset) == null) ? "" : this.findSource(mapping.getSqlExpression().toString(), dataset).getName());
                        final Label labeorgbranco14 = new Label(20, i, (this.findSource(mapping.getSqlExpression().toString(), dataset) == null) ? "" : this.findSource(mapping.getSqlExpression().toString(), dataset).getDataType().getName());
                        String tlength = "";
                        if (this.findSource(mapping.getSqlExpression().toString(), dataset) != null && this.findSource(mapping.getSqlExpression().toString(), dataset).getLength() != null) {
                            tlength = this.findSource(mapping.getSqlExpression().toString(), dataset).getLength().toString();
                        }
                        final Label labeorgbranco15 = new Label(21, i, tlength);
                        labeldsc3.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeldsc3);
                        labeldscf1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeldscf1);
                        labelfkk1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelfkk1);
                        labelpart1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelpart1);
                        labelpk2.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelpk2);
                        labelnlc1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelnlc1);
                        labelcolumn1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelcolumn1);
                        labeldatatype1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeldatatype1);
                        labellength1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labellength1);
                        labelscale1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelscale1);
                        sheet.addCell((WritableCell)labeltargemtp1);
                        labelpk1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelpk1);
                        labelnt1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelnt1);
                        labelinsert1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelinsert1);
                        labelupdate1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelupdate1);
                        labelscdd1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelscdd1);
                        labelexpm1.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labelexpm1);
                        labeorgbranco11.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeorgbranco11);
                        labeorgbranco12.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeorgbranco12);
                        labeorgbranco13.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeorgbranco13);
                        labeorgbranco14.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeorgbranco14);
                        labeorgbranco15.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labeorgbranco15);
                        ++i;
                    }
                    if (inft.getTargetDataStore().getUnderlyingOdiDataStore() != null) {
                        final Collection<OdiColumn> mappings2 = (Collection<OdiColumn>)inft.getTargetDataStore().getUnderlyingOdiDataStore().getColumns();
                        for (final OdiColumn mapping2 : mappings2) {
                            if (inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).getTargetSqlMappingExpression() != null) {
                                final Label labeltargemtp2 = new Label(0, i, "Mapping");
                                labeltargemtp2.setCellFormat((CellFormat)cellformattype);
                                final Label labelnlc2 = new Label(1, i, mapping2.getName());
                                final Label labeldsc4 = new Label(3, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).getBusinessRule());
                                final Label labeldscf2 = new Label(2, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).getUnderlyingOdiColumn().getDescription());
                                final Label labelcolumn2 = new Label(4, i, mapping2.getName());
                                final Label labeldatatype2 = new Label(5, i, mapping2.getDataType().getName());
                                final Label labellength2 = new Label(6, i, (mapping2.getLength() == null) ? "0" : mapping2.getLength().toString());
                                final Label labelscale2 = new Label(7, i, (mapping2.getScale() == null) ? "0" : mapping2.getScale().toString());
                                final Label labelpk3 = new Label(8, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).isUpdateKeyIndicator() ? "Sim" : "N\u00e3o");
                                final Label labelfkk2 = new Label(9, i, "");
                                final Label labelnt2 = new Label(10, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).isCheckNotNullIndicator() ? "Sim" : "N\u00e3o");
                                final Label labelpart2 = new Label(11, i, "");
                                final Label labelpk4 = new Label(12, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).isUpdateKeyIndicator() ? "Sim" : "N\u00e3o");
                                final Label labelinsert2 = new Label(13, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).isInsertIndicator() ? "Sim" : "N\u00e3o");
                                final Label labelupdate2 = new Label(14, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).isUpdateIndicator() ? "Sim" : "N\u00e3o");
                                String typescd2 = "";
                                if (inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).getUnderlyingOdiColumn().getScdType() != null) {
                                    final OdiColumn.ScdType type2 = inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).getUnderlyingOdiColumn().getScdType();
                                    if (type2.equals((Object)OdiColumn.ScdType.ADD_ROW_ON_CHANGE)) {
                                        typescd2 = "ADD";
                                    }
                                    else if (type2.equals((Object)OdiColumn.ScdType.OVERWRITE_ON_CHANGE)) {
                                        typescd2 = "UPDATE";
                                    }
                                    else if (type2.equals((Object)OdiColumn.ScdType.CURRENT_RECORD_FLAG)) {
                                        typescd2 = "FLAG";
                                    }
                                    else if (type2.equals((Object)OdiColumn.ScdType.START_TIMESTAMP)) {
                                        typescd2 = "END";
                                    }
                                    else if (type2.equals((Object)OdiColumn.ScdType.START_TIMESTAMP)) {
                                        typescd2 = "START";
                                    }
                                    else if (type2.equals((Object)OdiColumn.ScdType.SURROGATE_KEY)) {
                                        typescd2 = "SK";
                                    }
                                    else if (type2.equals((Object)OdiColumn.ScdType.NATURAL_KEY)) {
                                        typescd2 = "NK";
                                    }
                                }
                                final Label labelscdd2 = new Label(15, i, typescd2);
                                final Label labelexpm2 = new Label(16, i, inft.getTargetDataStore().getColumnOrNull(mapping2.getName()).getTargetSqlMappingExpression().toString());
                                final Label labeorgbranco16 = new Label(17, i, "");
                                final Label labeorgbranco17 = new Label(18, i, "");
                                final Label labeorgbranco18 = new Label(19, i, "");
                                final Label labeorgbranco19 = new Label(20, i, "");
                                final Label labeorgbranco20 = new Label(21, i, "");
                                labeldsc4.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeldsc4);
                                labeldscf2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeldscf2);
                                labelfkk2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelfkk2);
                                labelpart2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelpart2);
                                labelpk4.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelpk4);
                                labelnlc2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelnlc2);
                                labelcolumn2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelcolumn2);
                                labeldatatype2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeldatatype2);
                                labellength2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labellength2);
                                labelscale2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelscale2);
                                sheet.addCell((WritableCell)labeltargemtp2);
                                labelpk3.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelpk3);
                                labelnt2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelnt2);
                                labelinsert2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelinsert2);
                                labelupdate2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelupdate2);
                                labelscdd2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelscdd2);
                                labelexpm2.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labelexpm2);
                                labeorgbranco16.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeorgbranco16);
                                labeorgbranco17.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeorgbranco17);
                                labeorgbranco18.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeorgbranco18);
                                labeorgbranco19.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeorgbranco19);
                                labeorgbranco20.setCellFormat((CellFormat)cellformatbody);
                                sheet.addCell((WritableCell)labeorgbranco20);
                                ++i;
                            }
                        }
                    }
                    ++i;
                    final Label labefiltros = new Label(1, i, "Filtros");
                    sheet.addCell((WritableCell)labefiltros);
                    ++i;
                    final Label labefiltrostb = new Label(1, i, "Tabela");
                    labefiltrostb.setCellFormat((CellFormat)cellformat);
                    final Label labefiltroscampo = new Label(2, i, "Campo");
                    labefiltroscampo.setCellFormat((CellFormat)cellformat);
                    final Label labefiltrosf = new Label(3, i, "Sintaxe Filtro");
                    labefiltrosf.setCellFormat((CellFormat)cellformat);
                    sheet.addCell((WritableCell)labefiltrostb);
                    sheet.addCell((WritableCell)labefiltroscampo);
                    sheet.addCell((WritableCell)labefiltrosf);
                    ++i;
                    final Collection<Filter> filters = (Collection<Filter>)dataset.getFilters();
                    for (final Filter filter : filters) {
                        final Label labefilter = new Label(0, i, "Filter");
                        labefilter.setCellFormat((CellFormat)cellformattype);
                        final Label labefiltrostabela = new Label(1, i, filter.getAttachedDataStore().getName());
                        final Label labefiltroscp = new Label(2, i, (this.findSource(filter.getSqlExpression().toString(), dataset) == null) ? "" : this.findSource(filter.getSqlExpression().toString(), dataset).getName());
                        final Label labefiltrosexp = new Label(3, i, filter.getSqlExpression().toString());
                        labefiltrostabela.setCellFormat((CellFormat)cellformatbody);
                        labefiltroscp.setCellFormat((CellFormat)cellformatbody);
                        labefiltrosexp.setCellFormat((CellFormat)cellformatbody);
                        sheet.addCell((WritableCell)labefiltrostabela);
                        sheet.addCell((WritableCell)labefiltroscp);
                        sheet.addCell((WritableCell)labefiltrosexp);
                        sheet.addCell((WritableCell)labefilter);
                        ++i;
                    }
                    ++i;
                    final Label labelcomportamento = new Label(1, i, "Comportamento");
                    sheet.addCell((WritableCell)labelcomportamento);
                    ++i;
                    final Label labelcomportamento2 = new Label(1, i, "Comportamento");
                    sheet.addCell((WritableCell)labelcomportamento2);
                    final Label labelmc = new Label(2, i, "M\u00f3dulo de conhecimento");
                    sheet.addCell((WritableCell)labelmc);
                    labelcomportamento2.setCellFormat((CellFormat)cellformat);
                    labelmc.setCellFormat((CellFormat)cellformat);
                    ++i;
                    final Collection<SourceSet> lkms = (Collection<SourceSet>)dataset.getSourceSets();
                    for (final SourceSet lkm : lkms) {
                        final Label labelkm = new Label(0, i, "km");
                        labelkm.setCellFormat((CellFormat)cellformattype);
                        final Label labellkm2 = new Label(1, i, "LKM");
                        final Label labelkmtp = new Label(2, i, lkm.getLKM().getName());
                        sheet.addCell((WritableCell)labelkmtp);
                        sheet.addCell((WritableCell)labelkm);
                        sheet.addCell((WritableCell)labellkm2);
                        labellkm2.setCellFormat((CellFormat)cellformatbody);
                        labelkmtp.setCellFormat((CellFormat)cellformatbody);
                        ++i;
                    }
                    final Label labeckmtp = new Label(0, i, "km");
                    labeckmtp.setCellFormat((CellFormat)cellformattype);
                    final Label labeclkm2 = new Label(1, i, "CKM");
                    final Label labeclkm3 = new Label(2, i, inft.getTargetDataStore().getCKM().getName());
                    sheet.addCell((WritableCell)labeckmtp);
                    sheet.addCell((WritableCell)labeclkm3);
                    sheet.addCell((WritableCell)labeclkm2);
                    labeclkm2.setCellFormat((CellFormat)cellformatbody);
                    labeclkm3.setCellFormat((CellFormat)cellformatbody);
                    ++i;
                    final Label labeikmtp = new Label(0, i, "km");
                    labeikmtp.setCellFormat((CellFormat)cellformattype);
                    final Label labeilkm2 = new Label(1, i, "IKM");
                    final Label labeilkm3 = new Label(2, i, inft.getTargetDataStore().getIKM().getName());
                    sheet.addCell((WritableCell)labeikmtp);
                    sheet.addCell((WritableCell)labeilkm3);
                    sheet.addCell((WritableCell)labeilkm2);
                    labeilkm2.setCellFormat((CellFormat)cellformatbody);
                    labeilkm3.setCellFormat((CellFormat)cellformatbody);
                }
                w.write();
                w.close();
            }
        }
        lnr.close();
        inf.close();
        System.out.println("");
        System.out.println("Funda\u00e7\u00f5es utilizadas:");
        for (final String source2 : listasource) {
            System.out.println(source2);
        }
    }
}