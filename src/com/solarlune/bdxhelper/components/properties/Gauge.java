package com.solarlune.bdxhelper.components.properties;

import com.nilunder.bdx.Bdx;
import com.nilunder.bdx.Component;
import com.nilunder.bdx.GameObject;
import com.nilunder.bdx.State;
import com.nilunder.bdx.utils.Timer;

public class Gauge extends Component<GameObject> {

	public enum Adjust {  	// What happens if you adjust the maximum value (e.g. when VAL = 5 and MAX was 10, but MAX then changes to 20)
		NONE,    			// Don't do anything (M = 20, V = 5)
		RATIO,  			// Adds to value to maintain value-oldmax ratio (M = 20, V = 10)
		INVERSE_RATIO,		// Subtracts from value to maintain newmax-oldmax ratio (M = 20, V = 2.5)
		DIFFERENCE,  		// Sets value to maintain difference (M = 20, V = 15)
		REFILL,				// Sets value to match the new maximum (M = 20, V = 20)
		REFILL_UP,			// Sets value to match the new maximum only when the maximum increases (M = 20, V = 20; M = 7, V = 5)
	}

	private float value;
	private float maxValue;
	public boolean invulnerable = false;	// When a gauge has "invulnerable" set, sub()s don't work on it
	public float regenRate = 0;				// Regeneration per second
	public Adjust onMaxAdjust = Adjust.NONE;			// What to do when the max is adjusted
	public boolean allowNegatives = false;
    public boolean bottomedOut = false;		// If the gauge has dropped to 0
	private boolean prevBottomedOut = false;

    public Timer regenBoostTimer = new Timer(1);    // How much time should pass without sub()-ing which influences regenBoost
    public float regenBoost = 0;                    // How much of a regeneration boost to give in value or percentage (depending on regenMode)

    public float bottomOutRelease = 0;   // What value to release regen penalty when bottomed out
    public float bottomOutRegenCut = 0;  // What value to cut regen speed by when bottomed out

	public Gauge(GameObject g, float value, String name) {
		super(g);
		max(value);
		value(value);
		state = stateMain;
		this.name = name;
	}
	
	public Gauge(GameObject g) {
		this(g, 10, "Gauge");
	}
	
	public void add(float value){
		value(value() + value);
	}
	
	public void sub(float value){
		if (!invulnerable) {
            regenBoostTimer.restart();
            value(value() - value);
        }
	}

	public void value(float value){
		if (allowNegatives)
			this.value = Math.min(value, max());
		else
			this.value = Math.max(Math.min(value, max()), 0);
	}

	public float value() {
		return value;
	}
	
	public float valueAsPercentage(){
		return value() / max();
	}

	public void max(float value) {
		readjustStrat(value);
		this.maxValue = value;
	}
	
	public float max(){
		return maxValue;
	}
			
	private void readjustStrat(float newMax){
		if (onMaxAdjust == Adjust.RATIO){
			float ratio = newMax / maxValue;
			value *= ratio;
		}
		else if (onMaxAdjust == Adjust.DIFFERENCE) {
			float diff = newMax - maxValue;
			value += diff;
		}
		else if (onMaxAdjust == Adjust.REFILL)
			value = newMax;
		else if (onMaxAdjust == Adjust.REFILL_UP) {
			if (newMax > maxValue)
				value = newMax;
		}
		else if (onMaxAdjust == Adjust.INVERSE_RATIO) {
			float ratio = newMax / maxValue;
			value /= ratio;
		}
	}

	public boolean justEmptied(){
		return bottomedOut && !prevBottomedOut;
	}

	State stateMain = new State(){
		
		public void main() {

            if (bottomedOut)
                regenBoostTimer.restart();

			prevBottomedOut = bottomedOut;

            float regen = 0;

			if (regenRate != 0)
				regen = regenRate * Bdx.TICK_TIME;

            if (regenBoostTimer.done())
            regen += regenBoost * Bdx.TICK_TIME;

            if (value() <= 0)
                bottomedOut = true;

            if (value() >= bottomOutRelease)
                bottomedOut = false;

            if (bottomedOut)
                regen -= bottomOutRegenCut;

            add(regen);

        };
		
	};

}
