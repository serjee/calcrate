import java.io.File;

/**
 * Created by SEr on 09.07.2016.
 */
public class CalcRate {

    public static void main(String args[])
    {
        File f = new File("./ros.xml");
        if (f.exists() && !f.isDirectory()) {
            FileXML fXML = new FileXML();
            fXML.readXmlFile();
            new CalcRateForm(
                    fXML.getDeposit(),
                    fXML.getRate(),
                    fXML.getFixRate(),
                    fXML.getProfit(),
                    fXML.getDealModel(),
                    fXML.getCounter(),
                    fXML.getCntDealPlus(),
                    fXML.getCntDealMinus(),
                    fXML.getSumProfit(),
                    fXML.getWinRate()
            );
        } else {
            new CalcRateForm();
        }
    }
}
