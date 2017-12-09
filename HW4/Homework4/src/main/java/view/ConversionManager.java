package view;

import controller.ConverterFacade;
import model.ConversionRateDTO;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles all interaction with the account JSF page.
 */
@Named("conversionManager")
@ConversationScoped
public class ConversionManager implements Serializable {
    @EJB
    private ConverterFacade converterFacade;
    private ConversionRateDTO currentConversion;

    private Map<String,Object> listOfFromCurrencies;
    private Map<String,Object> listOfToCurrencies;
    private String currentFromCurrency;
    private String currentToCurrency;
    private Double currentCurrencyAmount;
    private String producedConversion;
    private String producedConversionNumber;
    private Exception transactionFailure;
    @Inject
    private Conversation conversation;

    @PostConstruct
    public void init() {
        changeListOfFromCurrenciesValues();
    }

    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    private void handleException(Exception e) {
        stopConversation();
        e.printStackTrace(System.err);
        transactionFailure = e;
    }

    /**
     * @return <code>true</code> if the latest transaction succeeded, otherwise
     * <code>false</code>.
     */
    public boolean getSuccess() {
        return transactionFailure == null;
    }

    /**
     * Returns the latest thrown exception.
     */
    public Exception getException() {
        return transactionFailure;
    }

    public void changeListOfFromCurrenciesValues(){
        ArrayList<String> listofnames;
        try {
            startConversation();
            transactionFailure = null;
            listofnames = converterFacade.findFromCurrencies();
        } catch (Exception e) {
            handleException(e);
            listofnames = null;
        }

        Map<String,Object> newmap = new LinkedHashMap<String,Object>();
        for (String name : listofnames) {
            newmap.put(name, name);
        }
        listOfFromCurrencies= newmap;
    }

    public void changeListOfToCurrenciesValues(){
        ArrayList<String> listofnames;
        try {
            startConversation();
            transactionFailure = null;
            listofnames= converterFacade.findToCurrencies(this.currentFromCurrency);
        } catch (Exception e) {
            handleException(e);
            listofnames= null;
        }
        Map<String,Object> newmap = new LinkedHashMap<String,Object>();
        for (String name : listofnames) {
            newmap.put(name, name);
        }
        listOfToCurrencies= newmap;
    }

    public void convert(){
        try {
            startConversation();
            transactionFailure = null;
            currentConversion = converterFacade.findByFromAndToCurrency(this.currentFromCurrency, this.currentToCurrency);
            this.producedConversion = this.currentCurrencyAmount+ " from " + currentConversion.getFromCurrency() + " to " +
                    currentConversion.getToCurrency() + " at the conversion rate "+ currentConversion.getConvRate()+ " is: ";
            this.producedConversionNumber = String.valueOf((this.currentCurrencyAmount *currentConversion.getConvRate()));

        } catch (Exception e) {
            handleException(e);
            currentConversion = null;
        }


    }

    /////////////////////// Getters ///////////////////////////////////////

    public String getCurrentFromCurrency() {
        return this.currentFromCurrency;
    }

    public String getCurrentToCurrency() {
        return this.currentToCurrency;
    }

    public Map<String,Object> getListFromCurrencyValue() {
        return listOfFromCurrencies;
    }

    public Map<String,Object> getListToCurrencyValue() {
        return listOfToCurrencies;
    }

    public Double getCurrentCurrencyAmount() {
        return currentCurrencyAmount;
    }

    public String getProducedConversion() {
        return producedConversion;
    }

    public String getProducedConversionNumber() {
        return producedConversionNumber;
    }

    ////////////////////// Setters ////////////////////////////////////////

    public void setCurrentFromCurrency(String currentFromCurrency) {
        this.currentFromCurrency = currentFromCurrency;
    }

    public void setCurrentToCurrency(String currentToCurrency) {
        this.currentToCurrency = currentToCurrency;
    }

    public void setCurrentCurrencyAmount(Double currentCurrencyAmount) { this.currentCurrencyAmount = currentCurrencyAmount; }

    public void setProducedConversion(String producedConversion){
        this.producedConversion = producedConversion;
    }

    public void setProducedConversionNumber(String producedConversionNumber) {
        this.producedConversionNumber = producedConversionNumber;
    }
}