public final class ServerConstants
{
	// checked against using equals(), order does not matter between, but should appear at top
	public static final String READY_TO_PLAY = 		"\1";
	public static final String GAME_IN_SESSION =	"\1\1";
	
	// checked against using startsWith(), must appear last to first
	public static final String ADD_PLAYER = 		"\1\1\1";
	public static final String DELETE_PLAYER = 		"\1\1\1\1";
	public static final String WAIT_BEFORE_PLAY = 	"\1\1\1\1\1";
	public static final String SET_TEAM = 			"\1\1\1\1\1\1";
	public static final String CREATE_BULLET = 		"\1\1\1\1\1\1\1";
	public static final String TERMINATE_BULLET = 	"\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_RIGHT = 	"\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_LEFT = 	"\1\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_DOWN = 	"\1\1\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_UP = 	"\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String UPDATE_BULLET = 		"\1\1\1\1\1\1\1\1\1\1\1\1\1";

	// used as a separator, must have special character
	public static final String NAME_SEPERATOR = 	"\2";
	
	// constant numerical fields
	public static final int PORT_NUMBER = 4444;
	public static final int WAIT_TIME = 10;
	public static final int BULLET_SIZE = 10;
	public static final int PLAYER_SIZE = 50;
	public static final int FRAME_SIZE = 600;
}