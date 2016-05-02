package com.Ryan.Calculator;

public class Complex
{
	public double real;
	public double imaginary;

	public double epsilon; // The tolerance for comparisons. Can be changed if a certain Complex is known to need a different epsilon
	// All instances of Complex created by a method of another will inherit the parent epsilon.

	static public double default_epsilon = 1E-6; // The default tolerance for all new Complex's. Can be changed if all or most will need a different epsilon. Defaults to a reasonable value

	// CONSTANTS
	final static public Complex ZERO=new Complex(0);
	final static public Complex ONE=new Complex(1);
	final static public Complex I=new Complex(0, 1);
	final static public Complex PI=new Complex(Math.PI);
	final static public Complex E=new Complex(Math.E);
	final static public Complex ERROR=new Complex(Double.NaN, Double.NaN); // Signifies an invalid operation

	public Complex()
	{
		real=0;
		imaginary=0;
		epsilon=default_epsilon;
	}

	public Complex(double Creal)
	{
		real=Creal;
		imaginary=0;
		epsilon=default_epsilon;
	}

	public Complex(double Creal, double Cimaginary)
	{
		real=Creal;
		imaginary=Cimaginary;
		epsilon=default_epsilon;
	}

	public Complex(double Creal, double Cimaginary, double Cepsilon)
	{
		real=Creal;
		imaginary=Cimaginary;
		epsilon=Cepsilon;
	}

	public Complex(Complex c)
	{
		real=c.real;
		imaginary=c.imaginary;
		epsilon=c.epsilon;
	}

	public Complex(Complex c, double Cepsilon)
	{
		real=c.real;
		imaginary=c.imaginary;
		epsilon=Cepsilon;
	}

	public String toString()
	{
		StringBuilder out=new StringBuilder();
		if (isReal())
			out.append(real);
		else if (isImaginary())
		{
			out.append(imaginary);
			out.append('i');
		}
		else
		{
			out.append(real);
			out.append( (imaginary==Math.abs(imaginary)) ? '+' : '-');
			if (epsilonNotEqualTo(Math.abs(imaginary), 1))
				out.append(Math.abs(imaginary));
			out.append('i');
		}
		return out.toString();
	}

	public static String toString (Complex c)
	{
		return c.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Complex)
		{
			Complex c=(Complex)o;
			return epsilonEqualTo(c.real, real) && epsilonEqualTo(c.imaginary, imaginary);
		}
		else if (o instanceof Double || o instanceof Integer)
			return epsilonEqualTo(real, (double)o);
		return super.equals(o);
	}

	public static Complex parseString(String str) // Parses a string from the format used above to return a Complex
	{
		if (str.equals("0")) // Special case, for simplicity
			return ZERO;

		double real=Double.parseDouble(str);
		double imaginary=0;
		if (str.contains("i"))
		{
			int idx=str.indexOf('i')-1;
			if (str.charAt(idx)=='+')
				imaginary=1;
			else if (str.charAt(idx)=='-')
				imaginary=-1;
			else
			{
				if (str.contains("+"))
					imaginary=Double.parseDouble(str.substring(str.indexOf('+')+1));
				else
					imaginary=Double.parseDouble(str.substring(str.indexOf("-", 1)));
			}
		}
		return new Complex(real, imaginary);
	}

	public String toNaiveString() // Ignores any special values and always returns a+bi, even when that makes no sense. Useful mostly with parseNaiveString()
	{
		return String.valueOf(real) +
				'+' +
				imaginary +
				'i';
	}

	public static Complex parseNaiveString(String str) // Create a complex based on the format used above. Much faster than parseString(), but does not play well with differently formatted inputs
	{
		double real;
		double imaginary;
		real=Double.parseDouble(str);
		int idx=str.indexOf('+');
		str=str.substring(++idx);
		imaginary=Double.parseDouble(str);
		return new Complex(real, imaginary);
	}

	private boolean epsilonEqualTo(double lhs, double rhs)
	{
		return lhs == rhs || Math.abs(lhs - rhs) < epsilon;
	}

	private boolean epsilonNotEqualTo(double lhs, double rhs)
	{
		return !epsilonEqualTo(lhs, rhs);
	}

	private boolean epsilonGreaterThan(double lhs, double rhs)
	{
		return (lhs-rhs)>epsilon;
	}

	private boolean epsilonGreaterThanOrEqualTo(double lhs, double rhs)
	{
		return epsilonGreaterThan(lhs, rhs) || epsilonEqualTo(lhs, rhs);
	}

	private boolean epsilonLessThan(double lhs, double rhs)
	{
		return !epsilonGreaterThanOrEqualTo(lhs, rhs);
	}

	private boolean epsilonLessThanOrEqualTo(double lhs, double rhs)
	{
		return !epsilonGreaterThan(lhs, rhs);
	}

	public boolean isReal()
	{
		return Math.abs(imaginary)<epsilon;
	}

	public boolean isImaginary()
	{
		return Math.abs(real)<epsilon;
	}

	public Complex addTo(Complex target)
	{
		real+=target.real;
		imaginary+=target.imaginary;
		return this;
	}

	public Complex subtractTo (Complex target) // This name doesn't make sense, but none do. In addition, operationTo will be defined for all binary operators,for consistency
	{
		real-=target.real;
		imaginary-=target.imaginary;
		return this;
	}

	public Complex multiplyTo (Complex target)
	{
		real*=target.real;
		real-=(imaginary*target.imaginary);

		imaginary*=target.real;
		imaginary+=(real+target.imaginary);

		return this;
	}

	public Complex multiplyWith (Complex target) // Alias, for logic's sake
	{
		return multiplyTo(target);
	}

	public Complex divideTo (Complex target)
	{
		real*=target.real;
		real-=(imaginary*target.imaginary);

		imaginary*=target.real;
		imaginary-=(real*target.imaginary);

		double fac=(target.real*target.real)-(target.imaginary*target.imaginary);
		if (fac==0) // Divide-by-zero check
			return ERROR;
		real/=fac;
		imaginary/=fac;

		return this;
	}

	/* FOR HISTORY
	The following function was produced as an alternative to the above one.
	It is no better, but it is far more clever


	public Complex divideTo(Complex target)
	{
		if (!target.isReal())
			return divideTo(target.rationalized());
		real/=target.real;
		imaginary/=target.real;
		return this;
	}
	*/

	public Complex divideWith (Complex target) // Alias, for logic's sake
	{
		return divideTo(target);
	}

	public static Complex add (Complex lhs, Complex rhs)
	{
		Complex out=new Complex(lhs, default_epsilon); // Reset the epsilon, to hide what we're doing and prevent odd behavior
		out.addTo(rhs);
		return out;
	}

	public static Complex subtract (Complex lhs, Complex rhs)
	{
		Complex out=new Complex(lhs, default_epsilon); // Reset the epsilon, to hide what we're doing and prevent odd behavior
		out.subtractTo(rhs);
		return out;
	}

	public static Complex multiply (Complex lhs, Complex rhs)
	{
		Complex out=new Complex(lhs, default_epsilon); // Reset the epsilon, to hide what we're doing and prevent odd behavior
		out.multiplyTo(rhs);
		return out;
	}

	public static Complex divide (Complex lhs, Complex rhs)
	{
		Complex out=new Complex(lhs, default_epsilon); // Reset the epsilon, to hide what we're doing and prevent odd behavior
		out.divideTo(rhs);
		return out;
	}

	public Complex conjugate ()
	{
		return new Complex(real, -imaginary, epsilon);
	}

	public Complex rationalize()
	{
		this.multiplyTo(conjugate());
		return this;
	}

	public Complex rationalized()
	{
		return multiply(this, conjugate());
	}

	public Complex negate()
	{
		real=-real;
		imaginary=-imaginary;
		return this;
	}

	public static Complex negate (Complex rhs)
	{
		Complex out =new Complex (rhs, default_epsilon); // Reset epsilon
		return out.negate();
	}

	public Complex round()
	{
		real=Math.round(real);
		imaginary=Math.round(imaginary);
		return this;
	}

	public static Complex round(Complex val)
	{
		Complex out=new Complex(val, default_epsilon); // Reset epsilon
		return out.round();
	}

	public Complex rounded()
	{
		return round(this);
	}

	public Complex floor()
	{
		real=Math.floor(real);
		imaginary=Math.floor(imaginary);
		return this;
	}

	public static Complex floor(Complex val)
	{
		Complex out=new Complex(val, default_epsilon); // Reset epsilon
		return out.floor();
	}

	public Complex floored()
	{
		return floor(this);
	}

	public Complex ceil()
	{
		real=Math.ceil(real);
		imaginary=Math.ceil(imaginary);
		return this;
	}

	public static Complex ceil(Complex val)
	{
		Complex out=new Complex(val, default_epsilon); // Reset epsilon
		return out.ceil();
	}

	public Complex ceiled() // This is a fantastic name for this. Definitely.
	{
		return ceil(this);
	}

	public Complex modulo (Complex target) // Doesn't modify the current instance
	{
		// Extension of the algorithm for general real modulo
		return subtract(this, multiply(floor(divide(this, target)), target));
	}

	public Complex moduloTo (Complex target) // Does modify the current instance
	{
		Complex result=modulo(target);
		real=result.real;
		imaginary=result.imaginary;
		return this;
	}

	public Complex moduloWith (Complex target) // For sanity
	{
		return moduloTo(target);
	}

	// All functions below here do not modify the object they're called on
	// I've changed my mind. Complex will now be immutable unless its data is directly touched
	// TODO: Make it so that this comment is at the top of the file, then delete it.

	public static Complex modulo (Complex lhs, Complex rhs) // Static wrapper
	{
		return lhs.modulo(rhs);
	}

	public double magnitude()
	{
		return Math.sqrt((real*real)+(imaginary*imaginary));
	}

	public double argument() // Result in radians
	{
		return Math.atan2(imaginary, real);
	}

	public double argumentd() // Result in degrees
	{
		return Math.toDegrees(Math.atan2(imaginary, real));
	}

	public static Complex fromPolar(double magnitude, double argument) // Argument in radians
	{
		return new Complex(magnitude*Math.cos(argument), magnitude*Math.sin(argument));
	}

	public static Complex fromPolarDegrees(double magnitude, double argument) // Sugar for argument in degrees
	{
		return fromPolar(magnitude, Math.toRadians(argument));
	}

	public static Complex fromExponential(double magnitude, double argument)
	{
		// The values are the same as polar form, just written differently
		// This just exists to let people forget that fact
		return fromPolar(magnitude, argument);
	}

	public static Complex fromExponentialDegrees(double magnitude, double argument)
	{
		// The values are the same as polar form, just written differently
		// This just exists to let people forget that fact
		return fromPolarDegrees(magnitude, argument);
	}

	public Complex ln() // Natural log
	{
		return new Complex(Math.log(magnitude()), argument());
	}

	public Complex log(Complex base) // Log in arbitrary base
	{
		return Complex.divide(ln(), base.ln());
	}

	public Complex log10() // Common (base 10) log
	{
		return log(new Complex(10));
	}

	public static Complex ln(Complex target) // Syntactic sugar
	{
		return target.ln();
	}

	public static Complex log10(Complex target) // More syntactic sugar
	{
		return target.log10();
	}

	public static Complex log(Complex target, Complex base) // Syntactic aspartame
	{
		return target.log(base);
	}

	public Complex sin()
	{
		return new Complex(Math.sin(real)*Math.cosh(imaginary), Math.cos(real)*Math.sinh(imaginary));
	}

	public Complex cos()
	{
		return new Complex(Math.cos(real)*Math.cosh(imaginary), -(Math.sin(real)*Math.sinh(imaginary)));
	}

	public Complex tan()
	{
		return divide(sin(), cos());
	}

	public static Complex sin(Complex target)
	{
		return target.sin();
	}

	public static Complex cos(Complex target)
	{
		return target.cos();
	}

	public static Complex tan(Complex target)
	{
		return target.tan();
	}

	public Complex sind()
	{
		return new Complex(Math.toRadians(real), Math.toRadians(imaginary)).sin();
	}

	public Complex cosd()
	{
		return new Complex(Math.toRadians(real), Math.toRadians(imaginary)).cos();
	}

	public Complex tand()
	{
		return new Complex(Math.toRadians(real), Math.toRadians(imaginary)).tan();
	}

	public static Complex sind(Complex target)
	{
		return target.sind();
	}

	public static Complex cosd(Complex target)
	{
		return target.cosd();
	}

	public static Complex tand(Complex target)
	{
		return target.tand();
	}

	public static Complex exp (Complex exponent)
	{
		// Slight extension of deMoivre's formula
		// Derived using the standard rules of real exponentiation
		return multiply(new Complex(Math.exp(exponent.real)),
				new Complex(Math.cos(exponent.imaginary), Math.sin(exponent.imaginary)));
	}

	public Complex exp()
	{
		return exp(this);
	}

	public static Complex expm1 (Complex exponent)
	{
		// This doesn't make a ton of sense, but it was at least very easy to implement
		return multiply(new Complex(Math.expm1(exponent.real)),
				new Complex(Math.cos(exponent.imaginary), Math.sin(exponent.imaginary)));
	}

	public Complex expm1()
	{
		return expm1(this);
	}

	public static Complex pow (double base, Complex exponent) // Necessary overload for the next function
	{
		// Slight extension of the last slight extension
		// Derived by representing base as e^ln(base)
		return multiply(new Complex(Math.pow(base, exponent.real)),
				new Complex(Math.cos(exponent.imaginary*Math.log(base)),
						Math.sin(exponent.imaginary*Math.log(base))));
	}

	public Complex pow(Complex exponent)
	{
		// We start with special cases that are easier to compute
		if (equals(ZERO))
			return ZERO; // Zero to the anything is zero
		if (exponent.equals(ZERO))
			return ONE; // Anything to the zeroth is one
		if (exponent.isReal())
		{
			if (isReal())
				return new Complex(Math.pow(real, exponent.real));
			if (isImaginary())
			{
				double coefficient=Math.pow(imaginary, exponent.real);
				if (Math.round(exponent.real)==exponent.real)
				{
					Complex[] results = new Complex[] // The results of raising I to powers
							{
									new Complex(coefficient), new Complex(0, coefficient),
									new Complex(-coefficient), new Complex(0, -coefficient)
							};
					return results [((int)exponent.real)%4];
				}
			}
		}

		// If execution proceeds to this point, then we have to use the general formula for complex powers
		// There is almost certainly a nicer way to write this. I'm so sorry
		// Formula used from Wolfram MathWorld: http://mathworld.wolfram.com/ComplexExponentiation.html, May 2 2016
		// TODO: Comment this silly mess
		double fac=exponent.real*argument()+.5*exponent.imaginary*Math.log((real*real)+(imaginary*imaginary));
		return multiply(new Complex(Math.pow((real*real)+(imaginary*imaginary), exponent.real/2)
									*Math.exp(-exponent.imaginary*argument())),
						new Complex(Math.cos(fac), Math.sin(fac)));
		// Yes, I know the indentation is messy. It's used mostly to clarify which expression pairs with which
	}

	public static Complex pow(Complex base, Complex exponent)
	{
		Complex out = new Complex(base, default_epsilon); // Reset epsilon
		return out.pow(exponent);
	}

	public Complex sqrt() // Syntactic sugar. For now at least, I'll probably find a better way to to this.. later
	{
		return pow(new Complex(0.5));
	}

	public static Complex sqrt(Complex out)
	{
		return pow(out, new Complex(0.5));
	}

	public Complex cbrt() // More syntactic sugar that should be improved later
	{
		return pow(new Complex(1/3));
	}

	public static Complex cbrt(Complex out)
	{
		return pow(out, new Complex(1/3));
	}

	public Complex square()
	{
		return multiply(this, this);
	}

	public static Complex square(Complex val)
	{
		return val.square();
	}

	public Complex asin()
	{
		return multiply(negate(I), ln(multiply(I, this).addTo(sqrt(subtract(ONE, square())))));
	}

	public static Complex asin(Complex target)
	{
		return target.asin();
	}

	public Complex acos()
	{
		return multiply(negate(I), ln(add(this, sqrt(subtract(ONE, square())))));
	}

	public static Complex acos(Complex target)
	{
		return target.acos();
	}

	public Complex atan()
	{
		return multiply(new Complex(0,.5), ln(add(this, I).divideTo(subtract(I, this))));
	}

	public static Complex atan(Complex target)
	{
		return target.atan();
	}

	public Complex sinh()
	{
		return new Complex(Math.sinh(real)*Math.cos(imaginary), Math.cosh(real)*Math.sin(imaginary));
	}

	public static Complex sinh(Complex target)
	{
		return target.sinh();
	}

	public Complex cosh()
	{
		return new Complex(Math.cosh(real)*Math.cos(imaginary), Math.sinh(real)*Math.sin(imaginary));
	}

	public static Complex cosh(Complex target)
	{
		return target.cosh();
	}

	public Complex tanh()
	{
		return divide(sinh(), cosh());
	}

	public static Complex tanh(Complex target)
	{
		return target.tanh();
	}

	public Complex asinh()
	{
		return ln(add(this, sqrt(add(square(this), ONE))));
	}

	public static Complex asinh(Complex target)
	{
		return target.asinh();
	}

	public Complex acosh()
	{
		return ln(add(this, sqrt(subtract(square(this), ONE))));
	}

	public static Complex acosh(Complex target)
	{
		return target.acosh();
	}

	public Complex atanh()
	{
		return divide(subtract(ln(add(ONE, this)), ln(subtract(ONE, this))), new Complex(2));
	}

	public static Complex atanh(Complex target)
	{
		return target.atanh();
	}

	public Complex toDegrees()
	{
		return new Complex (Math.toDegrees(real), Math.toDegrees(imaginary));
	}

	public static Complex toDegrees(Complex target)
	{
		return target.toDegrees();
	}

	public Complex toRadians()
	{
		return new Complex(Math.toRadians(real), Math.toRadians(imaginary));
	}

	public static Complex toRadians(Complex target)
	{
		return target.toRadians();
	}

	public double abs()
	{
		// Easy naming overload. The absolute value of a complex number is just its magnitude
		return magnitude();
	}

	public static double abs(Complex target)
	{
		return target.abs();
	}
}
