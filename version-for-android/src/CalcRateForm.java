import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;

/**
 * Created by SEr on 09.07.2016.
 */
public class CalcRateForm extends JFrame
{
    private JPanel panel1;              // корневой слой
    private JTextField tfCurDepo;       // депозит
    private JTextField tfCurRate;       // % ставки
    private JTextField tfCurProfit;     // % профита
    private JTextField tfNextRate;      // сумма ставки
    private JTextField tfNextProfit;    // сумма профита
    private JButton btnFalse;           // кнопка "НЕ УСПЕШНО"
    private JButton btnTrue;            // кнопка "УСПЕШНО"
    private JButton btnClear;           // кнопка "Очистить"
    private JButton btnSave;            // кнопка "Сохранить"
    private JTable tblRate;             // таблица результатов
    private JCheckBox chbCurFixRate;    // чекбокс "Зафиксировать ставку"
    private JPanel jTablePanel;         // блок для таблицы
    private JLabel fCurRate;            // ставка (меняется текст по чекбоксу)
    private JLabel fInfo;               // тулбар - инфо
    private TableModel model;           // модель таблицы

    private ArrayList<DealModel> dealModel;

    private double cDeposit;    // депозит
    private double cRate;       // % ставки или фикс.сумма
    private int cProfit;        // % выплаты
    private boolean pRate;      // ставка в % (true) или сумма (false)
    private double cSumProfit;  // профит (суммарно)
    private int cntDealPlus;    // количество сделок в +
    private int cntDealMinus;   // количество сделок в -
    private double winRate;     // % успешности
    private int counter;        // счетчик сделок

    /**
     * Конструктор (загрузка истории сделок из файла)
     */
    public CalcRateForm(double depo, double rate, int frate, int profit, ArrayList<DealModel> dealModel, int counter, int cntDealPlus, int cntDealMinus, double cSumProfit, double winRate) {

        // заполняем текстовые поля из файла
        tfCurDepo.setText(String.valueOf(depo));
        tfCurRate.setText(String.valueOf(rate));
        tfCurProfit.setText(String.valueOf(profit));
        this.dealModel = dealModel;
        if (frate==0) { // ставка в валюте
            this.pRate = false;
            fCurRate.setText("Ставка,$");
            chbCurFixRate.setSelected(true);
        } else { // ставка в %
            this.pRate = true;
        }

        // заполняем переменные информацией об истории
        this.counter = counter;
        this.cntDealPlus = cntDealPlus;
        this.cntDealMinus = cntDealMinus;
        this.cSumProfit = cSumProfit;
        this.winRate = winRate;

        FormInit();             // инициализация формы
        UpdateFromInputField(); // расчет следующей ставки
        UpdateDealsInfo();      // обновляем данные в таблице и в тулбаре
    }

    /**
     * Конструктор (файла с историей нет)
     */
    public CalcRateForm() {
        this.counter = 0;                               // счетчик сделок для истории
        this.dealModel =  new ArrayList<DealModel>();   // массив для хранения истории сделок
        this.pRate = true;                              // ставка в % по умолчанию
        FormInit();                                     // инициализация формы
    }

    /**
     * Инициализация формы
     */
    private void FormInit() {

        add(panel1);                                // добавляем корневую панель

        setTitle("CalcRate v.1.0");                 // заголовок окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // нужно для того, чтобы при закрытии окна закрывалась и программа, иначе она останется висеть в процессах
        setSize(520, 540);                          // размер окна
        setLocationRelativeTo(null);                // установка окна по центру
        setVisible(true);                           // установка видимости формы

        // контейнер для таблицы (необходим для отображения заголовков)
        jTablePanel.setLayout(new BorderLayout());
        jTablePanel.add(tblRate, BorderLayout.CENTER);
        jTablePanel.add(tblRate.getTableHeader(), BorderLayout.NORTH);

        // скроллинг для таблицы
        JScrollPane js = new JScrollPane(tblRate);
        js.setVisible(true);
        jTablePanel.add(js);

        // инициализация элементов тулбара
        btnClear = new JButton("Очистить");
        btnSave = new JButton("Сохранить");
        fInfo = new JLabel(GetToolbarInfo());

        // тулбар
        JToolBar toolBar = new JToolBar("Still draggable");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(fInfo);
        toolBar.addSeparator();
        toolBar.add(btnSave);
        toolBar.add(btnClear);
        jTablePanel.add(toolBar, "North");

        // Поле "Депозит" (изменение)
        tfCurDepo.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                DepoChange();
            }
            public void removeUpdate(DocumentEvent e) {
                DepoChange();
            }
            public void insertUpdate(DocumentEvent e) {
                DepoChange();
            }
            public void DepoChange() {
                if (isCheckInputField()) {  // проверка корректности заполненных полей пройдена
                    UpdateFromInputField(); // прозиводим расчет следующей ставки
                }
            }
        });

        // Поле "Ставка" (изменение)
        tfCurRate.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                RateChange();
            }
            public void removeUpdate(DocumentEvent e) {
                RateChange();
            }
            public void insertUpdate(DocumentEvent e) {
                RateChange();
            }
            public void RateChange() {
                if (isCheckInputField()) {  // проверка корректности заполненных полей пройдена
                    UpdateFromInputField(); // прозиводим расчет следующей ставки
                }
            }
        });

        // Поле "Выплата" (изменение)
        tfCurProfit.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                ProfitChange();
            }
            public void removeUpdate(DocumentEvent e) {
                ProfitChange();
            }
            public void insertUpdate(DocumentEvent e) {
                ProfitChange();
            }
            public void ProfitChange() {
                if (isCheckInputField()) {  // проверка корректности заполненных полей пройдена
                    UpdateFromInputField(); // прозиводим расчет следующей ставки
                }
            }
        });

        // Кнопка "НЕ УДАЧНО"
        btnFalse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isCheckInputField()) { // проверка корректности заполненных полей пройдена
                    UpdateFromDeal(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Error: All field should be filled!", "Error Massage", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Кнопка "УДАЧНО"
        btnTrue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isCheckInputField()) { // проверка корректности заполненных полей пройдена
                    UpdateFromDeal(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Error: All field should be filled!", "Error Massage", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Кнопка "Очистить"
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try { // удаляем файл сохранения, если он есть
                    File file = new File("./ros.xml");
                    file.delete();
                } catch (Exception x) {}

                // удаляем данные из моделей (чистим таблицу)
                dealModel =  new ArrayList<DealModel>();
                model = new DealTableModel(dealModel);
                tblRate.setModel(model);

                // обновляем счетчики и тулбар
                counter = 0;
                cSumProfit = 0;
                cntDealPlus = 0;
                cntDealMinus = 0;
                winRate = 0;
                fInfo.setText(GetToolbarInfo());
            }
        });

        // Кнопка "Сохранить"
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileXML fXML = new FileXML();
                fXML.writeXmlFile(dealModel, cDeposit, cRate, (pRate?1:0), cProfit, counter, cntDealMinus, cntDealPlus, cSumProfit, winRate);
            }
        });

        // Скролл для таблицы
        tblRate.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                tblRate.scrollRectToVisible(
                        new Rectangle(
                                0, (tblRate.getRowCount() - 1) * tblRate.getRowHeight(),
                                tblRate.getWidth(), tblRate.getRowHeight()
                        )
                );
            }
        });

        // Выделение чекбокса (фиксировать ставку)
        chbCurFixRate.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (chbCurFixRate.isSelected()) {
                    fCurRate.setText("Ставка,$");
                    pRate = false; // ставка в валюте
                    SetNextInputField();
                } else {
                    fCurRate.setText("Ставка,%");
                    pRate = true; // ставка в %
                    SetNextInputField();
                }
            }
        });
    }

    /**
     * Установка значения депозита
     *
     * @param cDeposit - сумма депозита
     */
    private void cDeposit(double cDeposit) {
        this.cDeposit = NumRound2(cDeposit);
    }

    /**
     * Проверка наличия и корректности введеных значений "Текущих данных"
     *
     * @return - результат проверки
     */
    private boolean isCheckInputField() {
        //JOptionPane.showMessageDialog(null, "Error: Please enter number bigger than 0", "Error Massage", JOptionPane.ERROR_MESSAGE);
        try {
            // Если поля все заполнены и соответствуют требуемым типам, то возвращаем "успех"
            if (!tfCurDepo.getText().isEmpty() && !tfCurRate.getText().isEmpty() && !tfCurProfit.getText().isEmpty()) {
                if (Double.parseDouble(tfCurDepo.getText()) >= 0 && Double.parseDouble(tfCurRate.getText()) >= 0 && Integer.parseInt(tfCurProfit.getText()) >= 0) {
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    /**
     * Пользователь зафиксировал сделку (обновляем переменные)
     *
     * @param isWin - флаг для успешной или не успешной сделки
     */
    private void UpdateFromDeal(boolean isWin) {

        counter++;                                          // увеличиваем счетчик
        double calcRate = CalcRate(cDeposit, cRate);        // расчет суммы ставки
        double calcProfit = CalcProfit(calcRate, cProfit);  // расчет суммы прибыли

        // расчет при выигрыше и проигрыше
        if (isWin) {
            cDeposit(cDeposit + calcProfit);                                        // к депозиту прибавляем сумму выигрыша
            dealModel.add(new DealModel(counter, cDeposit, calcRate, calcProfit));  // добавляем сделку
            cntDealPlus++;                                                          // количество сделок в +
            cSumProfit = NumRound2(cSumProfit + calcProfit);                        // профит (суммарно)
        } else {
            cDeposit(cDeposit - calcRate);                                          // из депозита вычитаем сумму ставки
            dealModel.add(new DealModel(counter, cDeposit, calcRate, -calcRate));   // добавляем сделку
            cntDealMinus++;                                                         // количество сделок в -
            cSumProfit = NumRound2(cSumProfit - calcRate);                          // профит (суммарно)
        }

        tfCurDepo.setText(String.valueOf(cDeposit));    // обновляем поле депозита
        SetNextInputField();                            // обновляем расчет следующей ставки
        UpdateDealsInfo();                              // обновляем данные в таблице и в тулбаре
    }

    /**
     * Обновление данных в таблице и в тулбаре
     */
    private void UpdateDealsInfo() {
        // обновляем данные в таблице
        model = new DealTableModel(dealModel);                              // обновляем модель
        tblRate.setModel(model);                                            // устанавливаем новую модель для таблицы
        tblRate.setDefaultRenderer(Object.class, new DealTableRender());    // управляем рендером таблицы в отдельном классе
        tblRate.getColumnModel().getColumn(0).setMaxWidth(36);              // фиксирукем ширину первого столбца

        // обновление информации для тулбара
        winRate = NumRound2((double) cntDealPlus / counter * 100);   // % успешности
        fInfo.setText(GetToolbarInfo());
    }

    /**
     * Пользователь отредактировал входящие данные (обновляем переменные) или загрузка из файла истории
     */
    private void UpdateFromInputField() {
        cDeposit(Double.parseDouble(tfCurDepo.getText()));  // текущий депозит
        cRate = Double.parseDouble(tfCurRate.getText());    // текущий % ставки или фикс. сумма
        cProfit = Integer.parseInt(tfCurProfit.getText());  // текущий % выплаты

        SetNextInputField();                                // обновляем расчет следующей ставки
    }

    /**
     *  Устанавливаем расчет следующей ставки в форму
     */
    private void SetNextInputField() {
        double nextRate = CalcRate(cDeposit, cRate);
        tfNextRate.setText(String.valueOf(nextRate));
        tfNextProfit.setText(String.valueOf(CalcProfit(nextRate, cProfit)));
    }

    /**
     * Расчет суммы ставки
     *
     * @param depo - депозит
     * @param rate - % ставки
     * @return - расчитанная сумма ставки
     */
    private double CalcRate(double depo, double rate) {
        if (pRate)
            return NumRound2(depo * rate / 100);
        else
            return NumRound2(rate);
    }

    /**
     * Расчет суммы профита
     *
     * @param sumRate - сумма ставки
     * @param profit - % профита
     * @return - расчитанная сумма профита
     */
    private double CalcProfit(double sumRate, int profit) {
        return NumRound2(sumRate * profit / 100);
    }

    /**
     * Округление до 2х десятичных знаков
     *
     * @param value - не округленное значение
     * @return - округленное значение до 2х десятичных знаков
     */
    private double NumRound2(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Информация для тулбара
     *
     * @return - html для лейбла тулбара
     */
    private String GetToolbarInfo() {
        return "<html>Сделок: " + String.valueOf(counter) + " [ <font color='red'>" + String.valueOf(cntDealMinus) + "</font> / <font color='green'>" + String.valueOf(cntDealPlus) + "</font> ] Профит: " + (cSumProfit>0?"<font color='green'>+":"<font color='red'>") + String.valueOf(cSumProfit) + "</font> Винрейт: " + (winRate>=60?"<font color='green'>":"<font color='red'>") + String.valueOf(winRate) + "%</font></html>";
    }
}


