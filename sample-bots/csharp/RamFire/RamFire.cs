using Robocode.TankRoyale.BotApi;
using Robocode.TankRoyale.BotApi.Events;

// ------------------------------------------------------------------
// RamFire
// ------------------------------------------------------------------
// A sample bot original made for Robocode by Mathew Nelson.
// Ported to Robocode Tank Royale by Flemming N. Larsen.
//
// Drives at robots trying to ram them. Fires when it hits them.
// ------------------------------------------------------------------
public class RamFire : Bot
{
    int turnDirection = 1; // clockwise (-1) or counterclockwise (1)

    // The main method starts our bot
    static void Main(string[] args)
    {
        new RamFire().Start();
    }

    // Constructor, which loads the bot settings file
    RamFire() : base(BotInfo.FromFile("RamFire.json")) { }

    // Called when a new round is started -> initialize and do some movement
    public override void Run()
    {
        // Set colors
        BodyColor = Color.FromHex("999");   // lighter gray
        TurretColor = Color.FromHex("888"); // gray
        RadarColor = Color.FromHex("666");  // dark gray

        // Spin the gun around slowly... forever
        while (IsRunning)
        {
            TurnLeft(5 * turnDirection);
        }
    }

    // We scanned another bot -> go ram it
    public override void OnScannedBot(ScannedBotEvent e)
    {
        TurnToFaceTarget(e.X, e.Y);
        var distance = DistanceTo(e.X, e.Y);
        Forward(distance + 5);

        Scan(); // Might want to move forward again!
    }

    // We have hit another bot -> turn to face robot, fire hard, and ram it again!
    public override void OnHitBot(HitBotEvent e)
    {
        TurnToFaceTarget(e.X, e.Y);

        // Determine a shot that won't kill the robot...
        // We want to ram it instead for bonus points
        if (e.Energy > 16)
            Fire(3);
        else if (e.Energy > 10)
            Fire(2);
        else if (e.Energy > 4)
            Fire(1);
        else if (e.Energy > 2)
            Fire(.5);
        else if (e.Energy > .4)
            Fire(.1);

        Forward(40); // Ram it again!
    }

    // Method that turns the bot to face the target at coordinate x,y, but also sets the
    // default turn direction used if no bot is being scanned within in the Run() method.
    private void TurnToFaceTarget(double x, double y)
    {
        double bearing = BearingTo(x, y);
        if (bearing >= 0)
            turnDirection = 1;
        else
            turnDirection = -1;

        TurnLeft(bearing);
    }
}