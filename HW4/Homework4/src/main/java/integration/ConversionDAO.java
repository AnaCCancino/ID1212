package integration;

import model.ConversionNotFoundException;
import model.ConversionRate;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.SynchronizationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles all interaction with the entity manager. No code outside of this class, except for the
 * JPA entities, shall have anything to do with JPA.
 */
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class ConversionDAO {
    @PersistenceContext(unitName = "currencyConverterPU")

    private EntityManager em;

    /**
     * Searches for the CurrencyRate with the specified fromCurrency and to Currency.
     * @param fromCurrency
     * @param toCurrency
     * @return
     */
    public ConversionRate findByFromAndToCurrency(String fromCurrency, String toCurrency) throws ConversionNotFoundException {
        ConversionRate conv = (ConversionRate) em.createNamedQuery("findByFromAndToCurrency")
                .setParameter("fromCurrency",fromCurrency)
                .setParameter("toCurrency", toCurrency).getSingleResult();
        if(conv != null){
            return conv;
        }
        else{
            throw new ConversionNotFoundException("The conversion rate from " + fromCurrency + " to " +toCurrency +" was not found");
        }
    }

    public ArrayList<String> findAllFromCurrency(){
        ArrayList<String> newarray = new ArrayList<String>();
        Collection<ConversionRate> conversionRates = em.createNamedQuery("findAllFromCurrency").getResultList();

        for (ConversionRate e: conversionRates) {
            newarray.add(e.getFromCurrency() );
        }
        return newarray;
    }

    public ArrayList<String> findToCurrency(String fromCurrency){
        ArrayList<String> newarray = new ArrayList<String>();
        Collection<ConversionRate> conversionRates = em.createNamedQuery("findTheCorrespondingToCurrency")
                .setParameter("fromCurrency", fromCurrency)
                .getResultList();

        for (ConversionRate e: conversionRates) {
            newarray.add(e.getToCurrency() );
        }
        return newarray;
    }
}
