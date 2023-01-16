package simulator.model;

import simulator.misc.Vector2D;

public class MassLossingBody extends Body{
	protected double lossFactor;		//Factor de perdida de masa
	protected double lossFrequency;		//Intervalo de tiempo desde que el cuerpo pierde masa
	protected double c;					//Contador
	
	public MassLossingBody(String id, Vector2D v, Vector2D p, double m, double factor, double frequency) {
		super(id, v, p, m);
		this.lossFactor = factor;
		this.lossFrequency = frequency;
		c=0;
	}
	
	//Movimiento del body
	void move(double t) {																						
		super.move(t);
		c +=t;
		if(this.c>=lossFrequency) {
			this.m= m*(1-lossFactor);
			c = 0;
		}
	}
	
}