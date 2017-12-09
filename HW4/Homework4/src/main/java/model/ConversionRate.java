package model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * A persistent representation of a Conversion Rate.
 */

@NamedQueries({
        @NamedQuery(
                name = "findByFromAndToCurrency",
                query = "SELECT conv FROM ConversionRate conv WHERE conv.fromCurrency LIKE :fromCurrency AND conv.toCurrency LIKE :toCurrency"
        ),
        @NamedQuery(
                name = "findAllFromCurrency",
                query = "SELECT conv FROM ConversionRate conv "
        ),
        @NamedQuery(
                name = "findTheCorrespondingToCurrency",
                query = "SELECT conv FROM ConversionRate conv WHERE conv.fromCurrency  LIKE :fromCurrency"
        )
})
@Entity(name = "ConversionRate")
public class ConversionRate implements ConversionRateDTO, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int convRateNo;
    private double convRate;
    private String fromCurrency;
    private String toCurrency;

    /**
     * Creates a new instance of ConversionRate
     */
    public ConversionRate() {
    }

    /**
     * Creates a new instance of ConversionRate
     */
    public ConversionRate(double convRate, String fromCurrency, String toCurrency) {
        this.convRate = convRate;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    /**
     * Get the value of fromCurrency
     *
     * @return the value of fromCurrency
     */
    //@Override
    public String getFromCurrency() {
        return fromCurrency;
    }

    /**
     * Get the value of toCurrency
     *
     * @return the value of toCurrency
     */
    //@Override
    public String getToCurrency() {
        return toCurrency;
    }

    /**
     * Get the value of convRate
     *
     * @return the value of convRate
     */
    //@Override
    public double getConvRate() {
        return convRate;
    }

    /**
     * Get the value of convRateNo.
     *
     * @return the value of convRateNo.
     */
    //@Override
    public int getConvRateNo() {
        return convRateNo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        return new Integer(convRateNo).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ConversionRate)) {
            return false;
        }
        ConversionRate other = (ConversionRate) object;
        return this.convRateNo == other.convRateNo;
    }

    @Override
    public String toString() {
        return "currencyConverter.model.ConversionRate[id=" + convRateNo + "]";
    }
}
