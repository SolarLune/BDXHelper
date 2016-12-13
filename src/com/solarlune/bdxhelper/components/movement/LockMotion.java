package com.solarlune.bdxhelper.components.movement;

import com.nilunder.bdx.Component;
import com.nilunder.bdx.GameObject;
import com.nilunder.bdx.State;

import javax.vecmath.Vector3f;

/**
 * Created by solarlune on 7/13/16.
 */
public class LockMotion extends Component<GameObject> {

    public String posAxes;
    public String velAxes;
    public Vector3f lockPosition = new Vector3f();
    public Vector3f lockVelocity = new Vector3f();

    public LockMotion(GameObject g, String velAxes, String posAxes) {
        super(g);
        this.posAxes = posAxes;
        this.velAxes = velAxes;
        lockPosition = g.position();
        state = mainState;
    }

    public LockMotion(GameObject g) {
        this(g, "y", "");
    }

    State mainState = new State() {

        public void main() {

            Vector3f vec = g.velocity();
            if (velAxes.toLowerCase().contains("x"))
                vec.x = lockVelocity.x;
            if (velAxes.toLowerCase().contains("y"))
                vec.y = lockVelocity.y;
            if (velAxes.toLowerCase().contains("z"))
                vec.z = lockVelocity.z;
            g.velocity(vec);

            vec = g.position();
            if (posAxes.toLowerCase().contains("x"))
                vec.x = lockPosition.x;
            if (posAxes.toLowerCase().contains("y"))
                vec.y = lockPosition.y;
            if (posAxes.toLowerCase().contains("z"))
                vec.z = lockPosition.z;
            g.position(vec);
        }

    };

}
