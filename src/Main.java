import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import java.util.ArrayList;

public class Main extends ApplicationFrame {
    private ArrayList<Double> priceList;

    public Main(String applicationTitle, String chartTitle) {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createXYLineChart(chartTitle, "Price", "Number of Cars", createDataset(), PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private XYDataset createDataset() {

        //get list of car prices on Craigslist in Atlanta
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.visit("https://atlanta.craigslist.org/search/cto");
            Elements prices = userAgent.doc.findEach("<span class =result-price>");
            String carPrice;
            priceList = new ArrayList<>();
            for (Element div : prices) {
                carPrice = div.innerText().substring(1); //removes $ symbol from price
                Double doubleCarPrice = Double.valueOf(carPrice); //converts string to double
                priceList.add(doubleCarPrice);
            }
        } catch (JauntException e) {
            System.err.println(e);
        }
        //plot normal distribution
        Function2D normal = new NormalDistributionFunction2D(mean(priceList), deviation(priceList));
        return DatasetUtilities.sampleFunction2D(normal, 0, 40000, 100, "Normal");
    }

    private Double mean(ArrayList<Double> list) {
        Double sum = 0.0;
        for (Double prices : list) {
            sum += prices;
        }
        return sum / list.size();
    }

    private Double deviation(ArrayList<Double> list) {
        Double sum = 0.00;
        for (Double data : list) {
            sum += Math.pow((data - mean(list)), 2);
        }
        return Math.sqrt(sum / (list.size() - 1));
    }

    public static void main(String[] args) {
        Main chart = new Main("Craigslist Cars Normal Distribution", "Normal Distribution of Car Prices on Craigslist");
        chart.pack();
        chart.setVisible(true);
    }
}
