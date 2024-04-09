package TeamRed.TimeManagementBE.domain;

public enum Role {
	OWNER("Owner"),
	USER("User"),
	VIEWER("Viewer");
	
	private final String displayName;
	
	Role(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}