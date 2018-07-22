/**
 * Created by SEr on 11.07.2016.
 */
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class FileXML {

    private double cDeposit;    // депозит
    private double cRate;       // % ставки или фикс.сумма
    private int cFixRate;       // ставка в % (1) или сумма (0)
    private int cProfit;        // % выплаты
    private ArrayList<DealModel> dealModel;
    private double cSumProfit;  // профит (суммарно)
    private int cntDealPlus;    // количество сделок в +
    private int cntDealMinus;   // количество сделок в -
    private double winRate;     // % успешности
    private int counter;        // счетчик сделок

    public void writeXmlFile(ArrayList<DealModel> list, double sDepo, double sRate, int sFixRate, int sProfit, int counter, int cntDealMinus, int cntDealPlus, double cSumProfit, double winRate) {

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("CalcRate");
            doc.appendChild(rootElement);

            // InputData Elements
            Element idata = doc.createElement("InputData");
            rootElement.appendChild(idata);

            // InputData - Deposit
            Element iDepo = doc.createElement("Deposit");
            iDepo.appendChild(doc.createTextNode(String.valueOf(sDepo)));
            idata.appendChild(iDepo);

            // InputData - Rate
            Element iRate = doc.createElement("Rate");
            iRate.appendChild(doc.createTextNode(String.valueOf(sRate)));
            idata.appendChild(iRate);

            // InputData - FixRate
            Element iRateFix = doc.createElement("FixRate");
            iRateFix.appendChild(doc.createTextNode(String.valueOf(sFixRate)));
            idata.appendChild(iRateFix);

            // InputData - Profit
            Element iProfit = doc.createElement("Profit");
            iProfit.appendChild(doc.createTextNode(String.valueOf(sProfit)));
            idata.appendChild(iProfit);

            // InfoData Elements
            Element infdata = doc.createElement("InfoData");
            rootElement.appendChild(infdata);

            // InfoData - Counter
            Element infCounter = doc.createElement("Counter");
            infCounter.appendChild(doc.createTextNode(String.valueOf(counter)));
            infdata.appendChild(infCounter);

            // InfoData - CntDealPlus
            Element infCntDealPlus = doc.createElement("CntDealPlus");
            infCntDealPlus.appendChild(doc.createTextNode(String.valueOf(cntDealPlus)));
            infdata.appendChild(infCntDealPlus);

            // InfoData - CntDealMinus
            Element infCntDealMinus = doc.createElement("CntDealMinus");
            infCntDealMinus.appendChild(doc.createTextNode(String.valueOf(cntDealMinus)));
            infdata.appendChild(infCntDealMinus);

            // InfoData - SumProfit
            Element infSumProfit = doc.createElement("SumProfit");
            infSumProfit.appendChild(doc.createTextNode(String.valueOf(cSumProfit)));
            infdata.appendChild(infSumProfit);

            // InfoData - WinRate
            Element infWinRate = doc.createElement("WinRate");
            infWinRate.appendChild(doc.createTextNode(String.valueOf(winRate)));
            infdata.appendChild(infWinRate);

            // Deals elements
            Element deals = doc.createElement("Deals");
            rootElement.appendChild(deals);

            for (DealModel dm : list) {

                // deal elements
                Element deal = doc.createElement("Deal");
                deals.appendChild(deal);

                Element num = doc.createElement("Num");
                num.appendChild(doc.createTextNode(String.valueOf(dm.getNum())));
                deal.appendChild(num);

                Element depo = doc.createElement("Depo");
                depo.appendChild(doc.createTextNode(String.valueOf(dm.getDepo())));
                deal.appendChild(depo);

                Element rate = doc.createElement("Rate");
                rate.appendChild(doc.createTextNode(String.valueOf(dm.getRate())));
                deal.appendChild(rate);

                Element profit = doc.createElement("Profit");
                profit.appendChild(doc.createTextNode(String.valueOf(dm.getProfit())));
                deal.appendChild(profit);

            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("./ros.xml"));
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public void readXmlFile() {

        dealModel = new ArrayList<DealModel>();

        try {

            File fXmlFile = new File("./ros.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // read inputs
            NodeList nItemData = doc.getElementsByTagName("InputData");
            Element eItemData = (Element) nItemData.item(0);
            cDeposit =  Double.parseDouble(eItemData.getElementsByTagName("Deposit").item(0).getTextContent());
            cRate = Double.parseDouble(eItemData.getElementsByTagName("Rate").item(0).getTextContent());
            cFixRate = Integer.parseInt(eItemData.getElementsByTagName("FixRate").item(0).getTextContent());
            cProfit = Integer.parseInt(eItemData.getElementsByTagName("Profit").item(0).getTextContent());

            // read info
            NodeList nInfoData = doc.getElementsByTagName("InfoData");
            Element eInfoData = (Element) nInfoData.item(0);
            counter = Integer.parseInt(eInfoData.getElementsByTagName("Counter").item(0).getTextContent());
            cntDealPlus = Integer.parseInt(eInfoData.getElementsByTagName("CntDealPlus").item(0).getTextContent());
            cntDealMinus = Integer.parseInt(eInfoData.getElementsByTagName("CntDealMinus").item(0).getTextContent());
            cSumProfit = Double.parseDouble(eInfoData.getElementsByTagName("SumProfit").item(0).getTextContent());
            winRate = Double.parseDouble(eInfoData.getElementsByTagName("WinRate").item(0).getTextContent());

            // read deals
            NodeList nList = doc.getElementsByTagName("Deal");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    dealModel.add(
                            new DealModel(
                                    Integer.parseInt(eElement.getElementsByTagName("Num").item(0).getTextContent()),
                                    Double.parseDouble(eElement.getElementsByTagName("Depo").item(0).getTextContent()),
                                    Double.parseDouble(eElement.getElementsByTagName("Rate").item(0).getTextContent()),
                                    Double.parseDouble(eElement.getElementsByTagName("Profit").item(0).getTextContent())
                            )
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getDeposit() {
        return cDeposit;
    }
    public double getRate() {
        return cRate;
    }
    public int getFixRate() {
        return  cFixRate;
    }
    public int getProfit() {
        return cProfit;
    }
    public ArrayList<DealModel> getDealModel() {
        return dealModel;
    }
    public int getCounter() {
        return counter;
    }
    public int getCntDealPlus() {
        return cntDealPlus;
    }
    public int getCntDealMinus() {
        return cntDealMinus;
    }
    public double getSumProfit() {
        return cSumProfit;
    }
    public double getWinRate() {
        return winRate;
    }

}
