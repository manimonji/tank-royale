package dev.robocode.tankroyale.botapi;

import dev.robocode.tankroyale.botapi.events.Condition;
import dev.robocode.tankroyale.botapi.internal.BotInternals;

import java.net.URI;

/**
 * Abstract bot class provides convenient methods for movement, turning, and firing the gun. Most
 * bots should inherit from this class.
 */
public abstract class Bot extends BaseBot implements IBot {

    private final BotInternals __botInternals = new BotInternals(this, super.__baseBotInternals);

    /**
     * Constructor for initializing a new instance of the Bot class, which should be used when
     * both BotInfo and server URL is provided through environment variables, i.e., when starting up
     * the bot using a booter. These environment variables must be set to provide the server URL
     * and bot information, and are automatically set by the booter tool for Robocode.
     *
     * <p>
     * <b>Example of how to set the predefined environment variables:</b><br>
     * <br>
     * SERVER_URL=ws://localhost:7654<br>
     * SERVER_SECRET=xzoEeVbnBe5TGjCny0R1yQ<br>
     * BOT_NAME=MyBot<br>
     * BOT_VERSION=1.0<br>
     * BOT_AUTHOR=fnl<br>
     * BOT_DESCRIPTION=Sample bot<br>
     * BOT_URL=https://mybot.somewhere.net<br>
     * BOT_COUNTRY_CODE=DK<br>
     * BOT_GAME_TYPES=melee,1v1<br>
     * BOT_PLATFORM=Java<br>
     * BOT_PROG_LANG=Java 8<br>
     * </p>
     */
    public Bot() {
        super();
    }

    /**
     * Constructor for initializing a new instance of the Bot class.
     * This constructor assumes the server URL and secret is provided by the environment
     * variables SERVER_URL and SERVER_SECRET.
     *
     * @param botInfo is the bot info containing information about your bot.
     */
    public Bot(final BotInfo botInfo) {
        super(botInfo);
    }

    /**
     * Constructor for initializing a new instance of the Bot class.
     *
     * @param botInfo   is the bot info containing information about your bot.
     * @param serverUrl is the server URL
     */
    public Bot(final BotInfo botInfo, URI serverUrl) {
        super(botInfo, serverUrl);
    }

    /**
     * Constructor for initializing a new instance of the Bot class.
     *
     * @param botInfo      is the bot info containing information about your bot.
     * @param serverUrl    is the server URL
     * @param serverSecret is the server secret
     */
    public Bot(final BotInfo botInfo, URI serverUrl, String serverSecret) {
        super(botInfo, serverUrl, serverSecret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isRunning() {
        return __botInternals.isRunning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTargetSpeed(double targetSpeed) {
        __botInternals.setTargetSpeed(targetSpeed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setForward(double distance) {
        __botInternals.setForward(distance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void forward(double distance) {
        __botInternals.forward(distance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setBack(double distance) {
        setForward(-distance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void back(double distance) {
        forward(-distance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double getDistanceRemaining() {
        return __botInternals.getDistanceRemaining();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTurnLeft(double degrees) {
        __botInternals.setTurnLeft(degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void turnLeft(double degrees) {
        __botInternals.turnLeft(degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTurnRight(double degrees) {
        setTurnLeft(-degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void turnRight(double degrees) {
        turnLeft(-degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double getTurnRemaining() {
        return __botInternals.getTurnRemaining();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTurnGunLeft(double degrees) {
        __botInternals.setTurnGunLeft(degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void turnGunLeft(double degrees) {
        __botInternals.turnGunLeft(degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTurnGunRight(double degrees) {
        setTurnGunLeft(-degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void turnGunRight(double degrees) {
        turnGunLeft(-degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double getGunTurnRemaining() {
        return __botInternals.getGunTurnRemaining();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTurnRadarLeft(double degrees) {
        __botInternals.setTurnRadarLeft(degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void turnRadarLeft(double degrees) {
        __botInternals.turnRadarLeft(degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTurnRadarRight(double degrees) {
        setTurnRadarLeft(-degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void turnRadarRight(double degrees) {
        turnRadarLeft(-degrees);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double getRadarTurnRemaining() {
        return __botInternals.getRadarTurnRemaining();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void fire(double firepower) {
        __botInternals.fire(firepower);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        __botInternals.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resume() {
        __botInternals.resume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scan() {
        __botInternals.scan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitFor(Condition condition) {
        __botInternals.waitFor(condition);
    }
}
