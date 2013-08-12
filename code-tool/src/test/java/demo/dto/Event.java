/*
 * This is the header!!!
 *
 */

package demo.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Robin Long
 */
public class Event
    implements Serializable
{
	/**
	 * Version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
    private java.math.BigDecimal id = null;
	/**
	 * title
	 */
    private String title = null;
	/**
	 * title_pound
	 */
    private String title_pound = null;
	/**
	 * start_date
	 */
    private java.util.Date start_date = null;

    public Event() {}

	public Event(final java.math.BigDecimal id, final String title, final String title_pound, final java.util.Date start_date) {
					this.id = id;
					this.title = title;
					this.title_pound = title_pound;
					this.start_date = start_date;
			}


    /**
     * @return the id
     */
    public java.math.BigDecimal getId()
    {
        return this.id;
    }

    /**
     * @param id 
     * 						the id to set
     */
    public void setId(final java.math.BigDecimal id)
    {
		this.id = id;
    }
    /**
     * @return the title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @param title 
     * 						the title to set
     */
    public void setTitle(final String title)
    {
		this.title = title;
    }
    /**
     * @return the title_pound
     */
    public String getTitle_pound()
    {
        return this.title_pound;
    }

    /**
     * @param title_pound 
     * 						the title_pound to set
     */
    public void setTitle_pound(final String title_pound)
    {
		this.title_pound = title_pound;
    }
    /**
     * @return the start_date
     */
    public java.util.Date getStart_date()
    {
        return this.start_date;
    }

    /**
     * @param start_date 
     * 						the start_date to set
     */
    public void setStart_date(final java.util.Date start_date)
    {
		this.start_date = start_date;
    }

	public String toString()
	{
		return new ToStringBuilder(this).appendSuper(super.toString()).append("id", this.id).append("title", this.title).append("title_pound", this.title_pound).append("start_date", this.start_date).toString();
	}
}
