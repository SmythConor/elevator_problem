/**
 * Person class for representing a person
 * @author Conor Smyth 12452382 <conor.smyth39@mail.dcu.ie>
 * @author Phil Brennan 12759011 <philip.brennan36@mail.dcu.ie>
 */
class Person implements Runnable {
	private Integer personId;
	private Double weight;
	private Integer arrivalTime;
	private Integer arrivalFloor;
	private Integer destinationFloor;
	private Double luggageWeight;

	public Person(boolean[] occupiedFloors) {
		this.personId = Generator.generateId();
		this.weight = Generator.generateWeight();
		this.arrivalTime = Generator.generateArrivalTime();
		this.arrivalFloor = Generator.generateFloor(occupiedFloors);
		this.destinationFloor = Generator.generateFloor(arrivalFloor);
		this.luggageWeight = Generator.generateWeight();
	}

	/**
	 * @return the personId
	 */
	public Integer getPersonId() {
		return personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	/**
	 * @return the weight
	 */
	public Double getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	/**
	 * @return the arrivalTime
	 */
	public Integer getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(Integer arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * @return the arrivalFloor
	 */
	public Integer getArrivalFloor() {
		return arrivalFloor;
	}

	/**
	 * @param arrivalFloor the arrivalFloor to set
	 */
	public void setArrivalFloor(Integer arrivalFloor) {
		this.arrivalFloor = arrivalFloor;
	}

	/**
	 * @return the destinationFloor
	 */
	public Integer getDestinationFloor() {
		return destinationFloor;
	}

	/**
	 * @param destinationFloor the destinationFloor to set
	 */
	public void setDestinationFloor(Integer destinationFloor) {
		this.destinationFloor = destinationFloor;
	}

	/**
	 * @return the luggageWeight
	 */
	public Double getLuggageWeight() {
		return luggageWeight;
	}

	/**
	 * @param luggageWeight the luggageWeight to set
	 */
	public void setLuggageWeight(Double luggageWeight) {
		this.luggageWeight = luggageWeight;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Person [");
		if(personId != null) {
			builder.append("personId=");
			builder.append(personId);
			builder.append(", ");
		}
		if(weight != null) {
			builder.append("weight=");
			builder.append(weight);
			builder.append(", ");
		}
		if(arrivalTime != null) {
			builder.append("arrivalTime=");
			builder.append(arrivalTime);
			builder.append(", ");
		}
		if(arrivalFloor != null) {
			builder.append("arrivalFloor=");
			builder.append(arrivalFloor);
			builder.append(", ");
		}
		if(destinationFloor != null) {
			builder.append("destinationFloor=");
			builder.append(destinationFloor);
			builder.append(", ");
		}
		if(luggageWeight != null) {
			builder.append("luggageWeight=");
			builder.append(luggageWeight);
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void run() {}
}
