package controller;

import integration.ConversionDAO;
import model.ConversionNotFoundException;
import model.ConversionRateDTO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;

/**
 * A controller. All calls to the model that are executed because of an action taken by the view.
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ConverterFacade {
    @EJB
    ConversionDAO converterDB;

    /**
     * Search for the specified Currency.
     * @param fromCurrency The string that says the currency from
     * @param toCurrency The string that says the currency to
     * @return If it was found.
     * @throws ConversionNotFoundException Of the conversion rate is not found
     */
    public ConversionRateDTO findByFromAndToCurrency(String fromCurrency, String toCurrency) throws ConversionNotFoundException {
        return converterDB.findByFromAndToCurrency(fromCurrency,toCurrency);
    }

    /**
     * Search for the from Currencies.
     *
     * @return The List they are any.
     * @throws EntityNotFoundException If the account was not found.
     */
    public ArrayList<String> findFromCurrencies() {
        return converterDB.findAllFromCurrency();
    }

    /**
     * Find the to Currencies
     * @return Array of toCurrency strings
     */
    public ArrayList<String> findToCurrencies(String fromCurrency) { return converterDB.findToCurrency( fromCurrency);}

}
