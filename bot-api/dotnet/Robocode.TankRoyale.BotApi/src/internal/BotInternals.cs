using System.Linq;
using System;
using System.Threading;
using Robocode.TankRoyale.BotApi.Events;
using static System.Double;

namespace Robocode.TankRoyale.BotApi.Internal
{
  internal sealed class BotInternals : IStopResumeListener
  {
    private readonly IBot bot;
    private readonly BaseBotInternals baseBotInternals;

    private Thread thread;
    private readonly object threadMonitor = new object();

    private double previousDirection;
    private double previousGunDirection;
    private double previousRadarDirection;

    private bool isOverDriving;

    private double savedPreviousDirection;
    private double savedPreviousGunDirection;
    private double savedPreviousRadarDirection;

    private double savedDistanceRemaining;
    private double savedTurnRemaining;
    private double savedGunTurnRemaining;
    private double savedRadarTurnRemaining;

    public BotInternals(IBot bot, BaseBotInternals baseBotInternals)
    {
      this.bot = bot;
      this.baseBotInternals = baseBotInternals;

      baseBotInternals.SetStopResumeHandler(this);

      BotEventHandlers botEventHandlers = baseBotInternals.BotEventHandlers;
      botEventHandlers.onNextTurn.Subscribe(OnNextTurn, 100);
      botEventHandlers.onRoundEnded.Subscribe(OnRoundEnded, 100);
      botEventHandlers.onGameEnded.Subscribe(OnGameEnded, 100);
      botEventHandlers.onDisconnected.Subscribe(OnDisconnected, 100);
      botEventHandlers.onHitWall.Subscribe(OnHitWall, 100);
      botEventHandlers.onHitBot.Subscribe(OnHitBot, 100);
      botEventHandlers.onDeath.Subscribe(OnDeath, 100);
    }

    private void OnNextTurn(TickEvent evt)
    {
      if (evt.TurnNumber == 1)
        OnFirstTurn();

      ProcessTurn();
    }

    private void OnFirstTurn()
    {
      StopThread(); // sanity before starting a new thread (later)
      ClearRemaining();
      StartThread();
    }

    private void ClearRemaining()
    {
      DistanceRemaining = 0d;
      TurnRemaining = 0d;
      GunTurnRemaining = 0d;
      RadarTurnRemaining = 0d;

      previousDirection = bot.Direction;
      previousGunDirection = bot.GunDirection;
      previousRadarDirection = bot.RadarDirection;
    }

    private void OnRoundEnded(RoundEndedEvent evt)
    {
      StopThread();
    }

    private void OnGameEnded(GameEndedEvent evt)
    {
      StopThread();
    }

    private void OnDisconnected(DisconnectedEvent evt)
    {
      StopThread();
    }

    private void ProcessTurn()
    {
      // No movement is possible, when the bot has become disabled
      if (bot.IsDisabled)
      {
        ClearRemaining();
      }
      else
      {
        UpdateTurnRemaining();
        UpdateGunTurnRemaining();
        UpdateRadarTurnRemaining();
        UpdateMovement();
      }
    }

    private void StartThread()
    {
      lock (threadMonitor)
      {
        thread = new Thread(bot.Run);
        IsRunning = true; // before starting thread!
        thread.Start();
      }
    }

    private void StopThread()
    {
      lock (threadMonitor)
      {
        if (thread == null) return;

        IsRunning = false;
        thread.Join(0);
        thread = null;
      }
    }

    private void OnHitWall(HitWallEvent evt)
    {
      DistanceRemaining = 0;
    }

    private void OnHitBot(HitBotEvent evt)
    {
      if (evt.IsRammed)
        DistanceRemaining = 0;
    }

    private void OnDeath(DeathEvent evt)
    {
      if (evt.VictimId == bot.MyId)
        StopThread();
    }

    internal bool IsRunning { get; private set; }

    internal double DistanceRemaining { get; private set; }

    internal double TurnRemaining { get; private set; }

    internal double GunTurnRemaining { get; private set; }

    internal double RadarTurnRemaining { get; private set; }

    internal void SetTargetSpeed(double targetSpeed)
    {
      if (IsNaN(targetSpeed))
        throw new ArgumentException("targetSpeed cannot be NaN");

      if (targetSpeed > 0)
        DistanceRemaining = PositiveInfinity;
      else if (targetSpeed < 0)
        DistanceRemaining = NegativeInfinity;
      else
        DistanceRemaining = 0;

      baseBotInternals.BotIntent.TargetSpeed = targetSpeed;
    }

    internal void SetForward(double distance)
    {
      if (IsNaN(distance))
        throw new ArgumentException("distance cannot be NaN");

      DistanceRemaining = distance;
      var speed = baseBotInternals.GetNewSpeed(bot.Speed, distance);
      baseBotInternals.BotIntent.TargetSpeed = speed;
    }

    internal void Forward(double distance)
    {
      if (bot.IsStopped)
        bot.Go();
      else
      {
        SetForward(distance);
        do
          bot.Go();
        while (IsRunning && DistanceRemaining != 0);
      }
    }

    internal void SetTurnLeft(double degrees)
    {
      if (IsNaN(degrees))
        throw new ArgumentException("degrees cannot be NaN");

      TurnRemaining = degrees;
      baseBotInternals.BotIntent.TurnRate = degrees;
    }

    internal void TurnLeft(double degrees)
    {
      if (bot.IsStopped)
        bot.Go();
      else
      {
        SetTurnLeft(degrees);
        do
          bot.Go();
        while (IsRunning && TurnRemaining != 0);
      }
    }

    internal void SetTurnGunLeft(double degrees)
    {
      if (IsNaN(degrees))
        throw new ArgumentException("degrees cannot be NaN");

      GunTurnRemaining = degrees;
      baseBotInternals.BotIntent.GunTurnRate = degrees;
    }

    internal void TurnGunLeft(double degrees)
    {
      if (bot.IsStopped)
        bot.Go();
      else
      {
        SetTurnGunLeft(degrees);
        do
          bot.Go();
        while (IsRunning && GunTurnRemaining != 0);
      }
    }

    internal void SetTurnRadarLeft(double degrees)
    {
      if (IsNaN(degrees))
        throw new ArgumentException("degrees cannot be NaN");

      RadarTurnRemaining = degrees;
      baseBotInternals.BotIntent.RadarTurnRate = degrees;
    }

    internal void TurnRadarLeft(double degrees)
    {
      if (bot.IsStopped)
        bot.Go();
      else
      {
        SetTurnRadarLeft(degrees);
        do
          bot.Go();
        while (IsRunning && RadarTurnRemaining != 0);
      }
    }

    internal void Fire(double firepower)
    {
      if (bot.SetFire(firepower))
        bot.Go();
    }

    internal void Scan()
    {
      bot.SetScan();
      var scan = baseBotInternals.BotIntent.Scan == true;
      bot.Go();

      if (scan && bot.Events.Any(e => e is ScannedBotEvent))
        throw new RescanException();
    }

    internal void WaitFor(Condition condition)
    {
      while (IsRunning && !condition.Test())
        bot.Go();
    }

    internal void Stop()
    {
      baseBotInternals.SetStop();
      bot.Go();
    }

    internal void Resume()
    {
      baseBotInternals.SetResume();
      bot.Go();
    }

    public void OnStop()
    {
      savedPreviousDirection = previousDirection;
      savedPreviousGunDirection = previousGunDirection;
      savedPreviousRadarDirection = previousRadarDirection;

      savedDistanceRemaining = DistanceRemaining;
      savedTurnRemaining = TurnRemaining;
      savedGunTurnRemaining = GunTurnRemaining;
      savedRadarTurnRemaining = RadarTurnRemaining;
    }

    public void OnResume()
    {
      previousDirection = savedPreviousDirection;
      previousGunDirection = savedPreviousGunDirection;
      previousRadarDirection = savedPreviousRadarDirection;

      DistanceRemaining = savedDistanceRemaining;
      TurnRemaining = savedTurnRemaining;
      GunTurnRemaining = savedGunTurnRemaining;
      RadarTurnRemaining = savedRadarTurnRemaining;
    }

    private void UpdateTurnRemaining()
    {
      var delta = bot.CalcDeltaAngle(bot.Direction, previousDirection);
      previousDirection = bot.Direction;

      if (Math.Abs(TurnRemaining) <= Math.Abs(delta))
        TurnRemaining = 0;
      else
      {
        TurnRemaining -= delta;
        if (IsNearZero(TurnRemaining))
          TurnRemaining = 0;
      }
      bot.TurnRate = TurnRemaining;
    }

    private void UpdateGunTurnRemaining()
    {
      var delta = bot.CalcDeltaAngle(bot.GunDirection, previousGunDirection);
      previousGunDirection = bot.GunDirection;

      if (Math.Abs(GunTurnRemaining) <= Math.Abs(delta))
        GunTurnRemaining = 0;
      else
      {
        GunTurnRemaining -= delta;
        if (IsNearZero(GunTurnRemaining))
          GunTurnRemaining = 0;
      }
      bot.GunTurnRate = GunTurnRemaining;
    }

    private void UpdateRadarTurnRemaining()
    {
      var delta = bot.CalcDeltaAngle(bot.RadarDirection, previousRadarDirection);
      previousRadarDirection = bot.RadarDirection;

      if (Math.Abs(RadarTurnRemaining) <= Math.Abs(delta))
        RadarTurnRemaining = 0;
      else
      {
        RadarTurnRemaining -= delta;
        if (IsNearZero(RadarTurnRemaining))
          RadarTurnRemaining = 0;
      }
      bot.RadarTurnRate = RadarTurnRemaining;
    }

    private void UpdateMovement()
    {
      if (IsInfinity(DistanceRemaining))
      {
        baseBotInternals.BotIntent.TargetSpeed =
          IsPositiveInfinity(DistanceRemaining) ? Constants.MaxSpeed : -Constants.MaxSpeed;
      }
      else
      {
        var distance = DistanceRemaining;

        // This is Nat Pavasant's method described here:
        // https://robowiki.net/wiki/User:Positive/Optimal_Velocity#Nat.27s_updateMovement
        var speed = baseBotInternals.GetNewSpeed(bot.Speed, distance);
        baseBotInternals.BotIntent.TargetSpeed = speed;

        // If we are over-driving our distance and we are now at velocity=0 then we stopped
        if (IsNearZero(speed) && isOverDriving)
        {
          DistanceRemaining = 0;
          distance = 0;
          isOverDriving = false;
        }

        // the overdrive flag
        if (Math.Sign(distance * speed) != -1)
          isOverDriving = baseBotInternals.GetDistanceTraveledUntilStop(speed) > Math.Abs(distance);

        DistanceRemaining = distance - speed;
      }
    }

    private static bool IsNearZero(double value) => Math.Abs(value) < .00001;
  }
}