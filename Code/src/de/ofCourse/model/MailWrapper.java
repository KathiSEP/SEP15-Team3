package de.ofCourse.model;

public class MailWrapper {
    
    private Integer unitId;
    
    
    public MailWrapper(Integer id, String email){
	unitId = id;
	mail = email;
	
    }
    
    
    
    /**
     * @return the unitId
     */
    public Integer getUnitId() {
        return unitId;
    }

    /**
     * @param unitId the unitId to set
     */
    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    /**
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    private String mail;
    
    
    
    

}
