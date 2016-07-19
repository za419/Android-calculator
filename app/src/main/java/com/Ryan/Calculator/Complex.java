package com.Ryan.Calculator;

import org.jetbrains.annotations.NotNull;

public class Complex
{
	private final double real;
	private final double imaginary;

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

	public double real()
	{
		return real;
	}

	public double imaginary()
	{
		return imaginary;
	}

	public String toString()
	{
		StringBuilder out=new StringBuilder();
		if (isReal())
			out.append(real);
		else if (isImaginary())
		{
			if (epsilonNotEqualTo(Math.abs(imaginary), 1))
				out.append(imaginary);
			else if (epsilonLessThan(imaginary, 0))
				out.append('-');
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

	@Override
	public int hashCode()
	{
		return ((Double)real).hashCode()^((Double)imaginary).hashCode();
	}

	public static Complex parseString(String str) // Parses a string from the format used above to return a Complex
	{
		if (str==null)
			return ERROR;

		if (str.equals("")) // Bug avoidance
			return ZERO;

		if (str.equals("0")) // Special case, for simplicity
			return ZERO;

		if (str.equals("i")) // Special case...
			return I;

		if (str.equals("-i")) // ... for simplicity.
			return new Complex(0, -1);

		double real=parseDoublePrefix(str);
		double imaginary=0;
		if (str.contains("i"))
		{
			if (str.equals(Double.toString(real)+'i'))
				return new Complex(0, real);
			int idx=str.indexOf('i')-1;
			if (str.charAt(idx)=='+')
				imaginary=1;
			else if (str.charAt(idx)=='-')
				imaginary=-1;
			else
			{
				if (str.contains("+")) // a+bi
					imaginary=parseDoublePrefix(str.substring(str.indexOf('+')+1));
				else if (str.contains("-") && str.charAt(0)!='-') // a-bi, but not -a+bi or -bi
					imaginary=parseDoublePrefix(str.substring(str.indexOf("-", 1)));
				else
					return new Complex(0, real);
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
		// MAKES NO GUARANTEE ABOUT BEHAVIOR WITH STRINGS NOT RETURNED BY toNaiveString OR EQUIVALENT!
		// USE AT YOUR OWN RISK
		double real;
		double imaginary;
		real=parseDoublePrefix(str);
		int idx=str.indexOf('+');
		str=str.substring(++idx);
		imaginary=parseDoublePrefix(str);
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
		return new Complex(real+target.real, imaginary+target.imaginary);
	}

	public Complex subtractTo (Complex target) // This name doesn't make sense, but none do. In addition, operationTo will be defined for all binary operators,for consistency
	{
		return new Complex(real-target.real, imaginary-target.imaginary);
	}

	public Complex multiplyTo (Complex target)
	{
		double re=real;
		double im=imaginary;

		re*=target.real;
		re-=(imaginary*target.imaginary);

		im*=target.real;
		im+=(real*target.imaginary);

		return new Complex(re, im);
	}

	public Complex multiplyWith (Complex target) // Alias, for logic's sake
	{
		return multiplyTo(target);
	}

	public Complex divideTo (Complex target)
	{
		double re=real;
		double im=imaginary;

		re*=target.real;
		re+=(imaginary*target.imaginary);

		im*=target.real;
		im-=(real*target.imaginary);

		double fac=(target.real*target.real)+(target.imaginary*target.imaginary);
		if (fac==0) // Divide-by-zero check
			return ERROR;
		re/=fac;
		im/=fac;

		return new Complex(re, im);
	}

	/* FOR HISTORY
	The following function was produced as an alternative to the above one.
	It is no better, but it is far more clever.
	This function no longer works with the current class definition, because it modifies the instance.


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
		return out.addTo(rhs);
	}

	public static Complex subtract (Complex lhs, Complex rhs)
	{
		Complex out=new Complex(lhs, default_epsilon); // Reset the epsilon, to hide what we're doing and prevent odd behavior
		return out.subtractTo(rhs);
	}

	public static Complex multiply (Complex lhs, Complex rhs)
	{
		Complex out=new Complex(lhs, default_epsilon); // Reset the epsilon, to hide what we're doing and prevent odd behavior
		return out.multiplyTo(rhs);
	}

	public static Complex divide (Complex lhs, Complex rhs)
	{
		Complex out=new Complex(lhs, default_epsilon); // Reset the epsilon, to hide what we're doing and prevent odd behavior
		return out.divideTo(rhs);
	}

	public Complex conjugate ()
	{
		return new Complex(real, -imaginary, epsilon);
	}

	public Complex rationalize()
	{
		return multiplyTo(conjugate());
	}

	public Complex rationalized()
	{
		return multiply(this, conjugate());
	}

	public Complex negate()
	{
		return new Complex(-real, -imaginary);
	}

	public static Complex negate (Complex rhs)
	{
		Complex out=new Complex (rhs, default_epsilon); // Reset epsilon
		return out.negate();
	}

	public Complex round()
	{
		return new Complex(Math.round(real), Math.round(imaginary));
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
		return new Complex(Math.floor(real), Math.floor(imaginary));
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
		return new Complex(Math.ceil(real), Math.ceil(imaginary));
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

	public Complex moduloTo (Complex target)
	{
		// Extension of the algorithm for general real modulo
		return subtract(this, multiply(floor(divide(this, target)), target));
	}

	public Complex moduloWith (Complex target) // For sanity
	{
		return moduloTo(target);
	}

	public static Complex modulo (Complex lhs, Complex rhs) // Static wrapper
	{
		return lhs.moduloTo(rhs);
	}

	public double magnitude()
	{
		return Math.sqrt((real*real)+(imaginary*imaginary));
	}

	public static double magnitude(Complex val)
	{
		return val.magnitude();
	}

	public double argument() // Result in radians
	{
		return Math.atan2(imaginary, real);
	}

	public static double argument(Complex val)
	{
		return val.argument();
	}

	public double argumentd() // Result in degrees
	{
		return Math.toDegrees(Math.atan2(imaginary, real));
	}

	public static double argumentd(Complex val)
	{
		return val.argumentd();
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

	public Complex ln1p()
	{
		return ln(add(ONE,this)); // There is probably a better way to do this
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

	public static Complex ln1p(Complex target)
	{
		return target.ln1p();
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
		if (equals(ONE))
			return ONE; // One to the anything is one
		if (exponent.isReal())
		{
			if (isReal()) {
				if (Math.pow(exponent.real, -1)%2==0 && real<0) // If our computation will generate an imaginary
					return new Complex(0, Math.pow(-real, exponent.real));
				return new Complex(Math.pow(real, exponent.real));
			}
			if (isImaginary())
			{
				if (Math.round(exponent.real)==exponent.real)
				{
					double coefficient=Math.pow(imaginary, exponent.real);
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
		// Formula used from Wolfram MathWorld: http://mathworld.wolfram.com/ComplexExponentiation.html, July 10 2016
		// TODO: Comment this silly mess
		double fac=exponent.real*argument()+.5*exponent.imaginary*Math.log((real*real)+(imaginary*imaginary));
		return multiply(new Complex(Math.pow((real*real)+(imaginary*imaginary), exponent.real/2)*
									Math.exp(-exponent.imaginary*argument())),
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
		return pow(out, new Complex(1.0/3.0));
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
		return multiply(negate(I), ln(add(this, sqrt(subtract(square(), ONE)))));
	}

	public static Complex acos(Complex target)
	{
		return target.acos();
	}

	public Complex atan()
	{
		return multiply(new Complex(0,.5), subtract(ln(subtract(ONE, multiply(I, this))),
													ln(add(ONE, multiply(I, this)))));
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

	// Helper methods
	private static double parseDoublePrefix(@NotNull String str) // Acts like parseDouble, except it will not fail on strings like "2.0i"
	{
		if ("".equals(str))
			return 0;
		StringBuilder proc = new StringBuilder();
		int i = 0;
		if (str.charAt(i) == '-') {
			proc.append('-');
			++i;
		}
		for (; i<str.length(); ++i)
		{
			Character c=str.charAt(i);
			if (!(Character.isDigit(c) || c=='.')) // Double in base 10
				break;
			proc.append(c);
		}
		return Double.parseDouble(proc.toString());
	}
}
