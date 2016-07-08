package com.Ryan.Calculator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigInteger;

public class MainActivity extends Activity
{
	public Complex currentValue=Complex.ZERO;

	/** Called when the activity is first created. */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
		{
			getActionBar().hide();
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				findViewById(R.id.mainLayout).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
		setZero();
	}

	public Complex parseComplex(String num)
	{
		if (num==null || num.indexOf("Error", 0)==0 || num.indexOf("ERROR", 0)==0)
			return Complex.ZERO;
		if ("Not prime".equals(num) || "Not prime or composite".equals(num) || "Not Gaussian prime".equals(num))
			return Complex.ZERO;
		if ("Prime".equals(num) || "Gaussian prime".equals(num))
			return Complex.ONE;
		if (num.charAt(num.length()-1)=='\u03C0')
		{
			if (num.length()==1)
				return Complex.PI;
			else if (num.length()==2 && num.charAt(0)=='-') // If the string is two long and the first character is a negation
				return Complex.negate(Complex.PI); // Return negative pi
			return Complex.multiply(parseComplex(num.substring(0, num.length()-1)), Complex.PI);
		}
		if (num.charAt(num.length()-1)=='e')
		{
			if (num.length()==1)
				return Complex.E;
			else if (num.length()==2 && num.charAt(0)=='-') // If the string is two long and the first character is a negation
				return Complex.negate(Complex.E); // Return negative e
			return Complex.multiply(parseComplex(num.substring(0, num.length()-1)), Complex.E);
		}
		try {
			return Complex.parseString(num);
		}
		catch (NumberFormatException e) {
			setText("ERROR: Invalid number");
			View v=findViewById(R.id.mainCalculateButton);
			v.setOnClickListener(null); // Cancel existing computation
			v.setVisibility(View.GONE); // Remove the button
			return Complex.ERROR;
		}
	}

	public String inIntTermsOfPi(double num)
	{
		if (num==0)
			return "0";
		double tmp=num/Math.PI;
		int n=(int)tmp;
		if (n==tmp)
		{
			if (n==-1) // If it is a negative, but otherwise 1
				return "-\u03C0"; // Return negative pi
			return (n==1 ? "" : Integer.toString(n))+"\u03C0";
		}
		else
			return Double.toString(num);
	}

	public String inIntTermsOfPi(Complex num)
	{
		if (num.equals(Complex.ZERO)) // Special case: Prevents "0+0i"
			return "0";
		if (num.isReal())
			return inIntTermsOfPi(num.real);
		if (num.isImaginary()) {
			if (num.imaginary==1)
				return "i";
			else if (num.imaginary==-1)
				return "-i";
			return inIntTermsOfPi(num.imaginary) + 'i';
		}
		String out=inIntTermsOfPi(num.real);
		if (num.imaginary>0)
			out+="+";
		out+=inIntTermsOfPi(num.imaginary)+'i';
		return out;
	}

	public String inIntTermsOfE(double num)
	{
		if (num==0)
			return "0";
		double tmp=num/Math.E;
		int n=(int)tmp;
		if (n==tmp)
		{
			if (n==-1) // If it is a negative, but otherwise 1
				return "-e"; // Return negative e
			return (n==1 ? "" : Integer.toString((int)tmp))+"e";
		}
		else
			return Double.toString(num);
	}

	public String inIntTermsOfE(Complex num)
	{
		if (num.equals(Complex.ZERO)) // Special case: Prevents "0+0i"
			return "0";
		if (num.isReal())
			return inIntTermsOfE(num.real);
		if (num.isImaginary()) {
			if (num.imaginary==1)
				return "i";
			else if (num.imaginary==-1)
				return "-i";
			return inIntTermsOfE(num.imaginary) + 'i';
		}
		String out=inIntTermsOfE(num.real);
		if (num.imaginary>0)
			out+="+";
		out+=inIntTermsOfE(num.imaginary)+'i';
		return out;
	}

	public String inIntTermsOfAny(double num)
	{
		if (Double.isNaN(num)) // "Last-resort" check
			return "ERROR: Nonreal or non-numeric result."; // Trap NaN and return a generic error for it.
		// Because of that check, we can guarantee that NaN's will not be floating around for more than one expression.

		String out=inIntTermsOfPi(num);
		if (!out.equals(Double.toString(num)))
			return out;
		else
			return inIntTermsOfE(num);
	}

	public String inIntTermsOfAny(Complex num)
	{
		if (num.equals(Complex.ZERO)) // Special case: Prevents "0+0i"
			return "0";
		if (num.isReal())
			return inIntTermsOfAny(num.real);
		if (num.isImaginary()) {
			if (num.imaginary==1)
				return "i";
			else if (num.imaginary==-1)
				return "-i";
			return inIntTermsOfAny(num.imaginary) + 'i';
		}
		String out=inIntTermsOfAny(num.real);
		if (num.imaginary>0)
			out+="+";
		out+=inIntTermsOfAny(num.imaginary)+'i';
		return out;
	}

	public void zero(View v)
	{
		setZero();
	}

	public void setZero(EditText ev)
	{
		setText("0", ev);
	}

	public void setZero()
	{
		setZero((EditText) findViewById(R.id.mainTextField));
	}

	public void setText(String n, EditText ev)
	{
		ev.setText(n);
		ev.setSelection(0, n.length()); // Ensure the cursor is at the end
	}

	public void setText(String n)
	{
		setText(n, (EditText) findViewById(R.id.mainTextField));
	}

	public void terms(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(parseComplex(ev.getText().toString())), ev);
	}

	public void decimal(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Complex.toString(parseComplex(ev.getText().toString())), ev);
	}

	public Complex getValue(final EditText ev) // Parses the content of ev into a double.
	{
		return parseComplex(ev.getText().toString().trim());
	}

	public void doCalculate(final EditText ev, OnClickListener ocl) // Common code for buttons that use the mainCalculateButton.
	{
		doCalculate(ev, ocl, Complex.ZERO);
	}

	public void doCalculate(final EditText ev, OnClickListener ocl, Complex n) // Common code for buttons that use the mainCalculateButton, setting the default value to n rather than zero.
	{
		setText(inIntTermsOfAny(n), ev);
		final Button b=(Button)findViewById(R.id.mainCalculateButton);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(ocl);
	}

	public void add(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		doCalculate(ev, new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setOnClickListener(null);
				String num = ev.getText().toString().trim();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(currentValue.addTo(parseComplex(num))), ev);
				v.setVisibility(View.GONE);
			}
		});
	}

	public void subtract(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(currentValue.subtractTo(parseComplex(num))), ev);
				v.setVisibility(View.GONE);
			}
		});
	}

	public void subtract2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(parseComplex(num).subtractTo(currentValue)), ev);
				v.setVisibility(View.GONE);
			}
		});
	}

	public void multiply(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(currentValue.multiplyTo(parseComplex(num))), ev);
				v.setVisibility(View.GONE);
			}
		});
	}

	public void divide(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if ("".equals(num))
					return;
				Complex n=parseComplex(num);
				if (n.equals(Complex.ZERO))
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(currentValue.divideTo(n)), ev);
				v.setVisibility(View.GONE);
			}
		}, Complex.ONE);
	}

	public void divide2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if ("".equals(num))
					return;
				Complex n=parseComplex(num);
				if (n.equals(Complex.ZERO))
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(n.divideTo(currentValue)), ev);
				v.setVisibility(View.GONE);
			}
		}, Complex.ONE);
	}

	public void remainder(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		if (!Complex.round(currentValue).equals(currentValue))
		{
			setText("Error: Parameter is not an integer: "+ev.getText(), ev);
			return;
		}
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if ("".equals(num))
					return;
				v.setVisibility(View.GONE);
				Complex tmp=parseComplex(num);
				if (!Complex.round(tmp).equals(tmp))
					setText("Error: Parameter is not an integer: "+num, ev);
				else if (Complex.round(tmp).equals(Complex.ZERO))
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(Complex.round(currentValue).modulo(Complex.round(tmp))), ev);
			}
		}, Complex.ONE);
	}

	public void remainder2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		if (!Complex.round(currentValue).equals(currentValue))
		{
			setText("Error: Parameter is not an integer: "+ev.getText(), ev);
			return;
		}
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if ("".equals(num))
					return;
				v.setVisibility(View.GONE);
				Complex tmp=parseComplex(num);
				if (!Complex.round(tmp).equals(tmp))
					setText("Error: Parameter is not an integer: "+num, ev);
				else if (Complex.round(currentValue).equals(Complex.ZERO))
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(Complex.round(tmp).modulo(Complex.round(currentValue))), ev);
			}
		}, Complex.ONE);
	}

	public void e(View v)
	{
		setText("e");
	}

	public void pi(View v)
	{
		setText("\u03C0");
	}

	public void i(View v) { setText("i"); }

	public void negate(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.negate(parseComplex(ev.getText().toString()))), ev);
	}

	public void sin(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Complex.sin(parseComplex(ev.getText().toString()))), ev);
	}

	public void cos(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Complex.cos(parseComplex(ev.getText().toString()))), ev);
	}

	public void tan(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Complex.tan(parseComplex(ev.getText().toString()))), ev);
	}

	public void arcsin(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Complex.asin(parseComplex(ev.getText().toString()))), ev);
	}

	public void arccos(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Complex.acos(parseComplex(ev.getText().toString()))), ev);
	}

	public void arctan(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Complex.atan(parseComplex(ev.getText().toString()))), ev);
	}

	public void exp(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfE(Complex.exp(parseComplex(ev.getText().toString()))), ev);
	}

	public void degrees(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText((Complex.toDegrees(parseComplex(ev.getText().toString()))).toString(), ev);
	}

	public void radians(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Complex.toRadians(parseComplex(ev.getText().toString()))), ev);
	}

	public void radians2(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex tmp=parseComplex(ev.getText().toString());
		tmp=Complex.divide(tmp, new Complex(180));
		setText(Complex.toString(tmp)+'\u03C0', ev);
	}

	public void ln(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfE(Complex.ln(parseComplex(ev.getText().toString()))), ev);
	}

	public void log(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.log10(parseComplex(ev.getText().toString()))), ev);
	}

	public void logb(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseComplex(ev.getText().toString());
		doCalculate(ev, new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setOnClickListener(null);
				String num = ev.getText().toString();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(Complex.log(currentValue, parseComplex(num))), ev);
				v.setVisibility(View.GONE);
			}
		}, new Complex(10));
	}

	public void logb2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseComplex(ev.getText().toString());
		doCalculate(ev,new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(Complex.log(parseComplex(num), currentValue)), ev);
				v.setVisibility(View.GONE);
			}
		}, new Complex(10));
	}

	public void round(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Complex.toString(Complex.round(parseComplex(ev.getText().toString()))));
	}

	public void sqrt(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex n=parseComplex(ev.getText().toString());
		setText(inIntTermsOfAny(Complex.sqrt(n)), ev);
	}

	public void cbrt(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.cbrt(parseComplex(ev.getText().toString()))), ev);
	}

	public void ceil(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Complex.toString(Complex.ceil(parseComplex(ev.getText().toString()))), ev);
	}

	public void floor(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Complex.toString(Complex.floor(parseComplex(ev.getText().toString()))), ev);
	}

	public void pow(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseComplex(ev.getText().toString());
		doCalculate(ev, new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setOnClickListener(null);
				String num = ev.getText().toString();
				if (!"".equals(num))
					setText(inIntTermsOfAny(Complex.pow(currentValue, parseComplex(num))), ev);
				v.setVisibility(View.GONE);
			}
		}, currentValue);
	}

	public void pow2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseComplex(ev.getText().toString());
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString();
				if (!"".equals(num))
					setText(inIntTermsOfAny(Complex.pow(parseComplex(num), currentValue)), ev);
				v.setVisibility(View.GONE);
			}
		}, currentValue);
	}

	public void abs (View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.abs(parseComplex(ev.getText().toString()))), ev);
	}

	public void sinh(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.sinh(parseComplex(ev.getText().toString()))), ev);
	}

	public void expm(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.expm1(parseComplex(ev.getText().toString()))), ev);
	}

	public void cosh(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.cosh(parseComplex(ev.getText().toString()))), ev);
	}

	public void tanh(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.tanh(parseComplex(ev.getText().toString()))), ev);
	}

	public void lnp(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Complex.ln1p(parseComplex(ev.getText().toString()))), ev);
	}

	public void square(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex num=parseComplex(ev.getText().toString());
		setText(inIntTermsOfAny(num.square()), ev);
	}

	public void cube(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex num=parseComplex(ev.getText().toString());
		setText(inIntTermsOfAny(Complex.multiply(num.square(), num)), ev);
	}

	public void isPrime(View v) // Standard primality, not Gaussian
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex m=parseComplex(ev.getText().toString());
		if (!m.isReal())
		{
			if (!m.isImaginary()) // M is zero
				setText("Not prime");
			else
				setText("Error: Cannot compute standard is prime for complex numbers");
			return;
		}
		double num=m.real;
		int n=(int)Math.floor(num);
		if (n!=num || n<1 || isDivisible(n,2)) {
			setText("Not prime");
			return;
		}
		if (n==1) {
			setText("Not prime or composite");
			return;
		}
		for (int i=3; i<=Math.sqrt(n); i+=2) {
			if (isDivisible(n, i)) {
				setText("Not prime");
				return;
			}
		}
		setText("Prime");
	}

	public void isGaussianPrime(View v) // Computes whether a prime number is a Gaussian prime
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex m=parseComplex(ev.getText().toString());
		boolean prime=false;
		if (Math.floor(m.real)==m.real && Math.floor(m.imaginary)==m.imaginary)
		{
			if (m.isReal())
			{
				int n=(int)Math.abs(m.real);
				if (isDivisible(n-3, 4))
				{
					prime=true;
					for (int i = 3; i <= Math.sqrt(n); i += 2) {
						if (isDivisible(n, i)) {
							prime = false;
							break;
						}
					}
				}
			}
			else if (m.isImaginary())
			{
				int n=(int)Math.abs(m.imaginary);
				if (isDivisible(n-3, 4))
				{
					prime=true;
					for (int i=3; i<=Math.sqrt(n); i+=2) {
						if (isDivisible(n, i)) {
							prime=false;
							break;
						}
					}
				}
			}
			else
			{
				double norm=m.magnitude();
				int n=(int) Math.floor(norm);
				if (n==norm)
				{
					if (n!=1 && !isDivisible(n, 2))
					{
						prime=true;
						for (int i=3; i<=Math.sqrt(n); i+=2) {
							if (isDivisible(n, i)) {
								prime=false;
								break;
							}
						}
					}
				}
			}
		}
		setText(prime ? "Gaussian prime" : "Not Gaussian prime");
	}

	public boolean isDivisible(int num, int den) {
		return num%den==0;
	}

	public double fastPow(double val, int power)
	{
		if (val==2)
			return fastPow(power).doubleValue();
		switch (power)
		{
		case 0:
			return 1;
		case 1:
			return val;
		case 2:
			return val*val;
		default:
			if (power<0)
				return 1/fastPow(val, -1*power);
			if (power%2==0)
				return fastPow(fastPow(val, 2), power>>1);
			return val*fastPow(val, power-1);
		}
	}

	public BigInteger fastPow(int pow) // 2 as base
	{
		return BigInteger.ZERO.flipBit(pow);
	}

	public void raise2(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex num=parseComplex(ev.getText().toString());
		if (num.isReal() && Math.round(num.real)==num.real) // Integer power. Use the fastpow() and a BigInteger.
			setText(fastPow((int)Math.round(num.real)).toString(), ev);
		else
			setText(Complex.toString(Complex.pow(2, num)), ev);
	}
}
