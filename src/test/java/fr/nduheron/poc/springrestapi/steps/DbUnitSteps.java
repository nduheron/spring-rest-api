package fr.nduheron.poc.springrestapi.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Transactional
public class DbUnitSteps {

    @Autowired
    private IDatabaseTester dbTester;

    @Given("^(.+) datasets$")
    public void loadDatasets(String datasetsfilename) throws Exception {
        String[] dataSets = datasetsfilename.split(",");
        IDataSet[] idataSets = new IDataSet[dataSets.length];
        for (int i = 0; i < dataSets.length; i++) {
            idataSets[i] = new FlatXmlDataSetBuilder()
                    .build(getClass().getResourceAsStream("/datasets/" + dataSets[i] + ".xml"));
        }

        dbTester.setDataSet(new CompositeDataSet(idataSets));
        dbTester.onSetup();
    }

    @After("@dbunit")
    public void closeDbTester() throws Exception {
        dbTester.onTearDown();
    }
}
