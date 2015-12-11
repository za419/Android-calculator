<<<<<<< HEAD
package com.Ryan.Calculator;

import android.annotation.TargetApi;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import java.math.*;

public class MainActivity extends Activity
{
	public double currentValue=0; // Holds the currently entered double.
	
    /** Called when the activity is first created. */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) // If on or above Honeycomb...
		{
			getActionBar().hide(); // Hide the action bar. We don't need it.
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) // If on or above ICS...
				findViewById(R.id.mainLayout).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE); // Set the UI to low profile.
		}
		setZero(); // Set the text to zero, allowing the app to start.
    }
	
	public double parseDouble(String num) // Function like Double.parseDouble(), except adding support for some constants.
	{
		if (num.indexOf("Error", 0)==0 || num.indexOf("ERROR", 0)==0) // Errors have a value of 0
			return 0;
		if (num.charAt(num.length()-1)=='\u03C0') // Check for the pi symbol, and use it to represent pi.
		{
			if (num.length()==1) // If that's the entire string...
				return Math.PI; // Its value is pi
			return parseDouble(num.substring(0, num.length()-1))*Math.PI; // Else, its value is that of the first part of the string times pi. This allows for "5e*pi" to be computed natively.
		}
		if (num.charAt(num.length()-1)=='e') // Use 'e' for e.
		{
			if (num.length()==1) // If that's the entire string...
				return Math.E; // Its value is e.
			return parseDouble(num.substring(0, num.length()-1))*Math.E; // Else, its value is that of the first part of the string times e. See above comment.
		}
		return Double.parseDouble(num); // If there is no special character, then the native parseDouble() will work.
	}
	
	public String inIntTermsOfPi(double num) // Returns the number as a string, either as-is or as an integer prefixed to pi.
	{
		if (num==0)
			return "0"; // Special case, because 0 is obviously special.
		double tmp=num/Math.PI;
		int n=(int)tmp;
		if (n==tmp) // If the quotient is an integer
			return (n==1 ? "" : Integer.toString(n))+"\u03C0"; // Return it either prefixed (n!=1) or omitted in front of pi.
		else
			return Double.toString(num); // Else, return it as a string.
	}
	
	public String inIntTermsOfE(double num) // Returns the number as a string, either as-is or as an integer prefixed to e.
	{
		if (num==0)
			return "0"; // Special case.
		double tmp=num/Math.E;
		int n=(int)tmp;
		if (n==tmp) // If the quotient is an integer
			return (n==1 ? "" : Integer.toString((int)tmp))+"e"; // Return it either prefixed (n!=1) or omitted in fron tof e.
		else
			return Double.toString(num); // Else, return it as a string.
	}
	
	public String inIntTermsOfAny(double num) // Returns the number either in terms of e or in terms of pi.
	{
		String out=inIntTermsOfPi(num);
		if (!out.equals(Double.toString(num))) // If it is in terms of pi
			return out; // Return it in terms of pi
		else
			return inIntTermsOfE(num); // Otherwise return it in terms of e
	}
	
	public void zero(View v) // Triggers the zeroing of the input, as a button click handler.
	{
		setZero();
	}
	
	public void setZero(EditText ev) // Set an arbitrary EditText to "0"
	{
		setText("0", ev);
	}
	
	public void setZero() // Zero the main input
	{
		setZero((EditText)findViewById(R.id.mainTextField));
	}
	
	public void setText(String n, EditText ev) // Set an arbitrary EditText to have the content n
	{
		ev.setText(n);
		ev.setSelection(0, n.length()); // Ensure the cursor is at the end
	}
	
	public void setText(String n) // Set the main input to have the content n
	{
		setText(n, (EditText)findViewById(R.id.mainTextField));
	}
	
	public void terms(View v) // Place the number in terms of e and pi
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(parseDouble(ev.getText().toString())), ev);
	}
	
	public void decimal(View v) // Place the number in decimals.
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
		setText(Double.toString(n));
		final Button b=(Button)findViewById(R.id.mainCalculateButton);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(ocl);
	}

	public void add(View v)
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
				if (num==null || "".equals(num))
					return;
				setText(inIntTermsOfAny(currentValue+parseDouble(num)), ev);
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
				if (num==null || "".equals(num))
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
				if (num==null || "".equals(num))
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
				if (num==null || "".equals(num))
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
				if (num==null || "".equals(num))
					return;
				setText(inIntTermsOfAny(currentValue/parseDouble(num)), ev);
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
				if (num==null || "".equals(num))
					return;
				setText(inIntTermsOfAny(parseDouble(num)/currentValue), ev);
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
				if (num==null || "".equals(num))
					return;
				v.setVisibility(View.GONE);
				double tmp=parseDouble(num);
				if (Math.round(tmp)!=tmp)
				{
					setText("Error: Parameter is not an integer: "+num, ev);
					return;
				}
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
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString().trim();
				if (num==null || "".equals(num))
					return;
				v.setVisibility(View.GONE);
				double tmp=parseDouble(num);
				if (Math.round(tmp)!=tmp)
				{
					setText("Error: Parameter is not an integer: "+num, ev);
					return;
				}
				setText(inIntTermsOfAny(Math.round(tmp)%Math.round(currentValue)), ev);
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
		setText(inIntTermsOfAny(-1*parseDouble(ev.getText().toString())), ev);
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
		doCalculate(ev,new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString();
				if (num==null || "".equals(num))
					return;
				setText(inIntTermsOfAny(Math.log(currentValue)/Math.log(parseDouble(num))), ev);
				v.setVisibility(View.GONE);
			}
		},10);
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
				if (num==null || "".equals(num))
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
		setText(inIntTermsOfAny(Math.sqrt(parseDouble(ev.getText().toString()))), ev);
	}

	public void cbrt(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(Math.cbrt(parseDouble(ev.getText().toString()))), ev);
	}

	public void ceil(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Long.toString((long)Math.ceil(parseDouble(ev.getText().toString()))), ev);
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
		doCalculate(ev, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				v.setOnClickListener(null);
				String num=ev.getText().toString();
				if (num==null || "".equals(num))
					return;
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
				if (num==null || "".equals(num))
					return;
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

	public void signum(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(Long.toString((long)Math.signum(parseDouble(ev.getText().toString()))), ev);
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
