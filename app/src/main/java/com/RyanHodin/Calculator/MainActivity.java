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
	public double currentValue=0;

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

	public double parseDouble(String num)
	{
		if (num==null || num.indexOf("Error", 0)==0 || num.indexOf("ERROR", 0)==0)
			return 0;
		if ("Not prime".equals(num) || "Not prime or composite".equals(num))
			return 0;
		if ("Prime".equals(num))
			return 1;
		if (num.charAt(num.length()-1)=='\u03C0')
		{
			if (num.length()==1)
				return Math.PI;
			else if (num.length()==2 && num.charAt(0)=='-') // If the string is two long and the first character is a negation
				return -Math.PI; // Return negative pi
			return parseDouble(num.substring(0, num.length()-1))*Math.PI;
		}
		if (num.charAt(num.length()-1)=='e')
		{
			if (num.length()==1)
				return Math.E;
			else if (num.length()==2 && num.charAt(0)=='-') // If the string is two long and the first character is a negation
				return -Math.E; // Return negative e
			return parseDouble(num.substring(0, num.length()-1))*Math.E;
		}
		try {
			return Double.parseDouble(num);
		}
		catch (NumberFormatException ex) {
			setText("ERROR: Invalid number");
			return 0;
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
		setText(inIntTermsOfAny(parseDouble(ev.getText().toString())), ev);
	}

	public void decimal(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Double.toString(parseDouble(ev.getText().toString())), ev);
	}

	public double getValue(final EditText ev) // Parses the content of ev into a double.
	{
		return parseDouble(ev.getText().toString().trim());
	}

	public void doCalculate(final EditText ev, OnClickListener ocl) // Common code for buttons that use the mainCalculateButton.
	{
		doCalculate(ev, ocl, 0);
	}

	public void doCalculate(final EditText ev, OnClickListener ocl, double n) // Common code for buttons that use the mainCalculateButton, setting the default value to n rather than zero.
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
				setText(inIntTermsOfAny(currentValue + parseDouble(num)), ev);
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
				setText(inIntTermsOfAny(currentValue-parseDouble(num)), ev);
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
				setText(inIntTermsOfAny(parseDouble(num)-currentValue), ev);
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
				setText(inIntTermsOfAny(currentValue*parseDouble(num)), ev);
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
				double n=parseDouble(num);
				if (n==0)
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(currentValue/n), ev);
				v.setVisibility(View.GONE);
			}
		});
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
				double n=parseDouble(num);
				if (n==0)
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(n/currentValue), ev);
				v.setVisibility(View.GONE);
			}
		});
	}

	public void remainder(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		if (Math.round(currentValue)!=currentValue)
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
				double tmp=parseDouble(num);
				if (Math.round(tmp)!=tmp)
					setText("Error: Parameter is not an integer: "+num, ev);
				else if (Math.round(tmp)==0)
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(Math.round(currentValue)%Math.round(tmp)), ev);
			}
		});
	}

	public void remainder2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=getValue(ev);
		if (Math.round(currentValue)!=currentValue)
		{
			setText("Error: Parameter is not an integer: "+ev.getText(), ev);
			return;
		}
		doCalculate(ev, new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setOnClickListener(null);
				String num = ev.getText().toString().trim();
				if ("".equals(num))
					return;
				v.setVisibility(View.GONE);
				double tmp = parseDouble(num);
				if (Math.round(tmp) != tmp)
					setText("Error: Parameter is not an integer: " + num, ev);
				else if (Math.round(currentValue) == 0)
					setText("Error: Divide by zero.");
				else
					setText(inIntTermsOfAny(Math.round(tmp) % Math.round(currentValue)), ev);
			}
		});
	}

	public void e(View v)
	{
		setText("e");
	}

	public void pi(View v)
	{
		setText("\u03C0");
	}

	public void negate(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(-1 * parseDouble(ev.getText().toString())), ev);
	}

	public void sin(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Math.sin(parseDouble(ev.getText().toString()))), ev);
	}

	public void cos(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Math.cos(parseDouble(ev.getText().toString()))), ev);
	}

	public void tan(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Math.tan(parseDouble(ev.getText().toString()))), ev);
	}

	public void arcsin(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Math.asin(parseDouble(ev.getText().toString()))), ev);
	}

	public void arccos(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Math.acos(parseDouble(ev.getText().toString()))), ev);
	}

	public void arctan(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Math.atan(parseDouble(ev.getText().toString()))), ev);
	}

	public void exp(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfE(Math.exp(parseDouble(ev.getText().toString()))), ev);
	}

	public void degrees(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(((Double)Math.toDegrees(parseDouble(ev.getText().toString()))).toString(), ev);
	}

	public void radians(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfPi(Math.toRadians(parseDouble(ev.getText().toString()))), ev);
	}

	public void radians2(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		double tmp=parseDouble(ev.getText().toString());
		tmp/=180;
		setText(Double.toString(tmp)+'\u03C0', ev);
	}

	public void ln(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfE(Math.log(parseDouble(ev.getText().toString()))), ev);
	}

	public void log(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.log10(parseDouble(ev.getText().toString()))), ev);
	}

	public void logb(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseDouble(ev.getText().toString());
		doCalculate(ev, new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setOnClickListener(null);
				String num = ev.getText().toString();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(Math.log(currentValue) / Math.log(parseDouble(num))), ev);
				v.setVisibility(View.GONE);
			}
		}, 10);
	}

	public void logb2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseDouble(ev.getText().toString());
		doCalculate(ev,new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString();
				if ("".equals(num))
					return;
				setText(inIntTermsOfAny(Math.log(parseDouble(num))/Math.log(currentValue)), ev);
				v.setVisibility(View.GONE);
			}
		}, 10);
	}

	public void round(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Long.toString(Math.round(parseDouble(ev.getText().toString()))));
	}

	public void sqrt(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		double n=parseDouble(ev.getText().toString());
		if (n<0)
			setText("ERROR: Complex result.");
		else
			setText(inIntTermsOfAny(Math.sqrt(n)), ev);
	}

	public void cbrt(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.cbrt(parseDouble(ev.getText().toString()))), ev);
	}

	public void ceil(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Long.toString((long) Math.ceil(parseDouble(ev.getText().toString()))), ev);
	}

	public void floor(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Long.toString((long)Math.floor(parseDouble(ev.getText().toString()))), ev);
	}

	public void pow(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseDouble(ev.getText().toString());
		doCalculate(ev, new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setOnClickListener(null);
				String num = ev.getText().toString();
				if (!"".equals(num))
					setText(inIntTermsOfAny(Math.pow(currentValue, parseDouble(num))), ev);
				v.setVisibility(View.GONE);
			}
		}, currentValue);
	}

	public void pow2(View v)
	{
		final EditText ev=(EditText)findViewById(R.id.mainTextField);
		currentValue=parseDouble(ev.getText().toString());
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString();
				if (!"".equals(num))
					setText(inIntTermsOfAny(Math.pow(parseDouble(num), currentValue)), ev);
				v.setVisibility(View.GONE);
			}
		}, currentValue);
	}

	public void abs (View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.abs(parseDouble(ev.getText().toString()))), ev);
	}

	public void sinh(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.sinh(parseDouble(ev.getText().toString()))), ev);
	}

	public void expm(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.expm1(parseDouble(ev.getText().toString()))), ev);
	}

	public void cosh(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.cosh(parseDouble(ev.getText().toString()))), ev);
	}

	public void tanh(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.tanh(parseDouble(ev.getText().toString()))), ev);
	}

	public void lnp(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.log1p(parseDouble(ev.getText().toString()))), ev);
	}

	public void square(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		double num=parseDouble(ev.getText().toString());
		setText(inIntTermsOfAny(num*num), ev);
	}

	public void cube(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		double num=parseDouble(ev.getText().toString());
		setText(inIntTermsOfAny(num*num*num), ev);
	}

	public void isPrime(View v) {
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		double num=parseDouble(ev.getText().toString());
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
		double num=parseDouble(ev.getText().toString());
		if (Math.round(num)==num) // Integer power. Use the fastpow() and a BigInteger.
			setText(fastPow((int)Math.round(num)).toString(), ev);
		else
			setText(Double.toString(Math.pow(2, num)), ev);
	}
}
