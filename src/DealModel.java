/**
 * Модель данных таблицы сделок
 */
public class DealModel {

    private int num;
    private double depo;
    private double rate;
    private double profit;

    public DealModel(int num, double depo, double rate, double profit) {
        this.setNum(num);
        this.setDepo(depo);
        this.setRate(rate);
        this.setProfit(profit);
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setDepo(double depo) {
        this.depo = depo;
    }

    public double getDepo() {
        return depo;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getProfit() {
        return profit;
    }

}
