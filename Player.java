class Player
{
	String team;
	int posX, posY;

	public Player(String team, int posX, int posY)
	{
		this.team = team;
		this.posX = posX;
		this.posY = posY;
	}

	public Player()
	{
		team = "";
		posX = posY = 275;
	}

	public static Player getNewPlayer(String input)
	{
		Player plyr = new Player();
		plyr.setPlayer(input);
		return plyr;
	}

	public void setPlayer(String input)
	{
		posX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		posY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		team = input;
	}

	public String toString()
	{
		return "" + posX + '\0' + posY + '\0' + team;
	}

	public double getDistanceTo(int x, int y)
	{
		return Math.sqrt(Math.pow(x - (posX + 25), 2) + Math.pow(y - (posY + 25), 2));
	}
}