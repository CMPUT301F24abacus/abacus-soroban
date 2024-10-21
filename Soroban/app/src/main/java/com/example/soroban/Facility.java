package com.example.soroban;

/**
 * Stores any relevant information that is associated to a facility.
 * @Author: Matthieu Larochelle
 * @Version: 1.0
 */
public class Facility {
    private final User owner;
    private String name;
    private EventList hostedEvents;

    /**
     * Constructor method for Facility.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public Facility(User owner){
        this.owner = owner;
        this.hostedEvents = new EventList();
    }

    /**
     * Destructor method for Facility.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public void destroy(){
        this.hostedEvents.destroy();  // Destroy all events hosted at the facility.
        this.hostedEvents = null;
    }

    /**
     * Getter method for Facility's owner.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: owner User object.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Getter method for Facility's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Return: name string.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for Facility's name.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param name: name string.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Add a user to an Facility's list of events.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @return : Result of successful addition to Facility's list of events.
     */
    public Boolean addHostedEvent(Event event){
        return hostedEvents.add(event);
    }

    /**
     * Remove a user from an Events's list of attendees.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @Param: Event.
     * @return : Result of successful removal from Facility's list of events.
     */
    public Boolean removeHostedEvent(Event event){
        return hostedEvents.remove(event);
    }

}
