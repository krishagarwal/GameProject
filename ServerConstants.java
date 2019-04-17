public final class ServerConstants
{
	// checked against using equals(), order does not matter between, but should appear at top
	public static final String READY_TO_PLAY = 		"\1";
	public static final String GAME_IN_SESSION =	"\1\1";
	
	// checked against using startsWith(), must appear last to first
	public static final String ADD_CHARACTER = 		"\1\1\1";
	public static final String DELETE_CHARACTER = 	"\1\1\1\1";
	public static final String WAIT_BEFORE_PLAY = 	"\1\1\1\1\1";
	public static final String SET_TEAM = 			"\1\1\1\1\1\1";
	public static final String CREATE_BULLET = 		"\1\1\1\1\1\1\1";
	public static final String TERMINATE_BULLET = 	"\1\1\1\1\1\1\1\1";
	public static final String UPDATE_CHARACTER = 	"\1\1\1\1\1\1\1\1\1";

	// used as a separator, must have special character
	public static final String NAME_SEPERATOR = 	"\2";
	
	public static final int WAIT_TIME = 10;
}