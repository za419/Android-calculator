package com.Ryan.Calculator;

public class Complex
{
	public double real;
	public double imaginary;

	public double epsilon; // The tolerance for comparisons. Can be changed if a certain Complex is known to need a different epsilon
	// All instances of Complex created by a method of another will inherit the parent epsilon.

	static public double default_epsilon = 1E-6; // The default tolerance for all new Complex's. Can be changed if all or most will need a different epsilon. Defaults to a reasonable value

	// CONSTANTS
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

	public static Complex parseString(String str) // Parses a string from the format used above to return a Complex
	{
		if (str.equals("0")) // Special case, for simplicity
			return new Complex(0);

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
		StringBuilder out=new StringBuilder();
		out.append(real);
		out.append('+');
		out.append(imaginary);
		out.append('i');
		return out.toString();
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
}
