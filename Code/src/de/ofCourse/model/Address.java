/**
 * This package represents all the models which are used
 */
package de.ofCourse.model;

/**
 * contains all information about the address of Users and Courses
 * 
 * @author Sebastian
 *
 */
public class Address {
    
    public Address() {
	
    }

    /**
     * stores the id of the address
     */
    private int id;
    /**
     * stores the country where the user lives or where the course will be
     * hold
     */
    private String country;
    /**
     * stores the city where the user lives or where the course will be hold
     */
    private String city;
    /**
     * 
     * stores the street where the user lives or where the course will be hold
     */
    private String street;
    /**
     * stores the houseNumber where the user lives or where the course
     * will be hold
     */
    private Integer houseNumber;
    /**
     * stores the zipdCode where the user lives or where the course will be
     * hold
     */
    private Integer zipCode;
    
    
    //TODO comments
    private String location;

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((city == null) ? 0 : city.hashCode());
	result = prime * result + ((country == null) ? 0 : country.hashCode());
	result = prime * result
		+ ((houseNumber == null) ? 0 : houseNumber.hashCode());
	result = prime * result + id;
	result = prime * result + ((street == null) ? 0 : street.hashCode());
	result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Address other = (Address) obj;
	if (city == null) {
	    if (other.city != null)
		return false;
	} else if (!city.equals(other.city))
	    return false;
	if (country == null) {
	    if (other.country != null)
		return false;
	} else if (!country.equals(other.country))
	    return false;
	if (houseNumber == null) {
	    if (other.houseNumber != null)
		return false;
	} else if (!houseNumber.equals(other.houseNumber))
	    return false;
	if (id != other.id)
	    return false;
	if (street == null) {
	    if (other.street != null)
		return false;
	} else if (!street.equals(other.street))
	    return false;
	if (zipCode == null) {
	    if (other.zipCode != null)
		return false;
	} else if (!zipCode.equals(other.zipCode))
	    return false;
	return true;
    }

    /**
     * Returns the value of the attribute <code>id</code>.
     * 
     * @return the id of the address
     */
    public int getId() {
	return this.id;
    }
    
    /**
     * Returns the value of the attribute <code>country</code>.
     * 
     * @return the country where the user lives or course will be hold
     */
    public String getCountry() {
	return this.country;
    }

    /**
     * Returns the value of the attribute <code>city</code>.
     * 
     * @return the city where the user lives or the course will be hold
     */
    public String getCity() {
	return this.city;
    }

    /**
     * Returns the value of the attribute <code>street</code>.
     * 
     * @return the street where the user lives or the course will be hold
     */
    public String getStreet() {
	return this.street;
    }

    /**
     * Returns the value of the attribute <code>houseNumber</code>.
     * 
     * @return the houseNumber where the user lives or the course will be hold
     */
    public Integer getHouseNumber() {
	return this.houseNumber;
    }

    /**
     * Returns the value of the attribute <code>zipCode</code>.
     * 
     * @return the zipCode where the user lives or the course will be hold
     */
    public Integer getZipCode() {
	return this.zipCode;
    }

    /**
     * Sets the value of the attribute <code>id</code>.
     * 
     * @param id
     *          the new id to set
     */
    public void setId(int id) {
	this.id = id;
    }
    
    /**
     * Sets the value of the attribute <code>country</code>.
     * 
     * @param country
     *          new country to set
     */
    public void setCountry(String country) {
	this.country = country;
    }

    /**
     * Sets the value of the attribute <code>city</code>.
     * 
     * @param city
     *          the new city to set
     */
    public void setCity(String city) {
	this.city = city;
    }

    /**
     * Sets the value of the attribute <code>street</code>.
     * 
     * @param street
     *          the new street to set
     */
    public void setStreet(String street) {
	this.street = street;
    }

    /**
     * Sets the value of the attribute <code>houseNumber</code>.
     * 
     * @param houseNumber
     *          the new houseNumber to set
     */
    public void setHouseNumber(Integer houseNumber) {
	this.houseNumber = houseNumber;
    }

    /**
     * Sets the value of the attribute <code>zipCode</code>.
     * 
     * @param zipCode
     *          the new zipCode to set
     */
    public void setZipCode(Integer zipCode) {
	this.zipCode = zipCode;
    }

   
}
