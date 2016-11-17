package edu.gatech.bms;

/**
 * Created by houqixuan on 3/7/16.
 */
public class Movie {

    /**
     * MATH major
     */
    private static final String MATH = "MATH";
    /**
     * CS major
     */
    private static final String CS = "CS";
    /**
     * ECE major
     */
    private static final String ECE = "ECE";
    /**
     * CHEM major
     */
    private static final String CHEM = "CHEM";
    /**
     * ISYE major
     */
    private static final String ISYE = "ISYE";
    /**
     * CSE major
     */
    private static final String CSE = "CSE";
    /**
     * MATH average
     */
    private double MATH_average;
    /**
     * MATH total
     */
    private Integer MATH_total;
    /**
     * CS average
     */
    private  double CS_average;
    /**
     * CS total
     */
    private Integer CS_total;
    /**
     * ECE average
     */
    private double ECE_average;
    /**
     * ECE total
     */
    private Integer ECE_total;
    /**
     * CHEM average
     */
    private double CHEM_average;
    /**
     * CHEM total
     */
    private Integer CHEM_total;
    /**
     * CSE average
     */
    private double CSE_average;
    /**
     * CSE total
     */
    private Integer CSE_total;
    /**
     * ISYE average
     */
    private double ISYE_average;
    /**
     * ISYE total
     */
    private Integer ISYE_total;
    /**
     * movie name
     */
    private String movieName;
    /**
     * average
     */
    private double average;
    /**
     * total
     */
    private Integer total;
    /**
     * object id
     */
    private String objectId;

    /**
     * setter for movie name
     * @param movie movie name
     */
    public void setMovieName(String movie) {
        movieName = movie;
    }

    /**
     * getter for average rating
     * @param major major to set average rating
     * @return major's avarage rating
     */
    public double getAverageRating(String major) {
        if (major.equals(CS)) {
            return CS_average;
        } else if (major.equals(MATH)) {
            return MATH_average;
        } else if (major.equals(ECE)) {
            return ECE_average;
        } else  if (major.equals(CHEM)) {
            return CHEM_average;
        } else if (major.equals(CSE)) {
            return CSE_average;
        } else if (major.equals(ISYE)) {
            return ISYE_average;
        }
        return 0;
    }

    /**
     * setter for total
     * @param aTotal a total
     */
    public void setTotal(Integer aTotal) {
        total = aTotal;
    }

    /**
     * setter for average
     * @param aAverage an average
     */
    public void setAverage(double aAverage) {
        average = aAverage;
    }

    /**
     * getter for total
     * @param major the major to get total
     * @return major's total
     */
    public Integer getTotal(String major) {
        if (major.equals(CS)) {
            return CS_total;
        } else if (major.equals(MATH)) {
            return MATH_total;
        } else if (major.equals(ECE)) {
            return ECE_total;
        } else  if (major.equals(CHEM)) {
            return CHEM_total;
        } else if (major.equals(CSE)) {
            return CSE_total;
        } else if (major.equals(ISYE)) {
            return ISYE_total;
        }
        return 0;
    }

    /**
     * setter for CS average rating
     * @param pCS_average CS average rating
     */
    public void setCS_average( double pCS_average ) {
        this.CS_average = pCS_average;
    }

    /**
     * setter for CS total
     * @param pCS_total CS total
     */
    public void setCS_total( Integer pCS_total ) {
        this.CS_total = pCS_total;
    }

    /**
     * setter for MATH average rating
     * @param pMATH_average MATH average rating
     */
    public void setMATH_average( double pMATH_average ) {
        this.MATH_average = pMATH_average;
    }

    /**
     * setter for MATH total
     * @param pMATH_total MATH total
     */
    public void setMATH_total( Integer pMATH_total ) {
        this.MATH_total = pMATH_total;
    }

    /**
     * setter for total for different majors
     * @param major major to set total
     * @param ptotal total
     */
    public void setTotal(String major, Integer ptotal) {
        if (major.equals(CS)) {
            CS_total = ptotal;
        } else if (major.equals(MATH)) {
            MATH_total = ptotal;
        } else if (major.equals(ECE)) {
            ECE_total = ptotal;
        } else  if (major.equals(CHEM)) {
            CHEM_total = ptotal;
        } else if (major.equals(CSE)) {
            CSE_total = ptotal;
        } else if (major.equals(ISYE)) {
            ISYE_total = ptotal;
        }

    }

    /**
     * setter for average rating for different majors
     * @param major major to set average
     * @param paverage the average rating
     */
    public void setAverage(String major, double paverage) {
        if (major.equals(CS)) {
            CS_average = paverage;
        } else if (major.equals(MATH)) {
            MATH_average = paverage;
        } else if (major.equals(ECE)) {
            ECE_average = paverage;
        } else  if (major.equals(CHEM)) {
            CHEM_average = paverage;
        } else if (major.equals(CSE)) {
            CSE_average = paverage;
        } else if (major.equals(ISYE)) {
            ISYE_average = paverage;
        }
    }

    /**
     * getter for MATH average
     * @return MATH average
     */
    public double getMATH_average() {
        return MATH_average;
    }

    /**
     * getter for MATH total
     * @return MATH total
     */
    public Integer getMATH_total() {
        return MATH_total;
    }

    /**
     * getter for CS average
     * @return CS average
     */
    public double getCS_average() {
        return CS_average;
    }

    /**
     * getter for CS total
     * @return CS total
     */
    public Integer getCS_total() {
        return CS_total;
    }

    /**
     * getter for ECE average
     * @return ECE average
     */
    public double getECE_average() {
        return ECE_average;
    }

    /**
     * getter for ECE total
     * @return ECE total
     */
    public Integer getECE_total() {
        return ECE_total;
    }

    /**
     * getter for CHEM average
     * @return CHEM average
     */
    public double getCHEM_average() {
        return CHEM_average;
    }

    /**
     * getter for CHEM total
     * @return CHEM total
     */
    public Integer getCHEM_total() {
        return CHEM_total;
    }

    /**
     * getter for CSE average
     * @return CSE average
     */
    public double getCSE_average() {
        return CSE_average;
    }

    /**
     * getter for CSE total
     * @return CSE total
     */
    public Integer getCSE_total() {
        return CSE_total;
    }

    /**
     * getter for ISYE average
     * @return ISYE average
     */
    public double getISYE_average() {
        return ISYE_average;
    }

    /**
     * getter for ISYE total
     * @return ISYE total
     */
    public Integer getISYE_total() {
        return ISYE_total;
    }

    /**
     * getter for movie name
     * @return movie name
     */
    public String getMovieName() {
        return movieName;
    }

    /**
     * getter for average
     * @return average rating
     */
    public double getAverage() {
        return average;
    }

    /**
     * getter for total
     * @return total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * getter for object id
     * @return object id
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * setter for object id
     * @param pobjectId object id
     */
    public void setObjectId(String pobjectId) {
        this.objectId = pobjectId;
    }
}
