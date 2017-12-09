package model;

/**
 * The views read-only view of an ConversionRate model.
 */
public interface ConversionRateDTO {
    /**                                     
     * Gets the number of this conversion rate.
     *
     * @return the conversion rate id number.
     */
    int getConvRateNo();

    /**
     * Gets the convertion rate.
     *
     * @return the convertion rate.
     */
    double getConvRate();

    /**
     * Gets the Currency from which we want to convert.
     *
     * @return the Currency from which we want to convert.
     */
    String getFromCurrency();

    /**
     * Gets the Currency to which we want to convert.
     *
     * @return the Currency to which we want to convert.
     */
    String getToCurrency();

}
