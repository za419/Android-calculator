package com.Ryan.Calculator;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity
{
	private Complex currentValue=Complex.ZERO;

	/** Called when the activity is first created. */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) // If we have an action bar...
		{
			ActionBar ab=getActionBar();
			if (ab!=null)
				ab.hide(); // Hide it. It doesn't participate in our layout.
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) // If we are on or above ICS...
				findViewById(R.id.mainLayout).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE); // Set low profile
		}
		setZero(); // Set the input field to zero

		InputFilter filter=new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (source!=null && source.toString().contains("!")) { // Filter exclamation points to i
					SpannableString ch=new SpannableString(source.toString().replace('!', 'i'));
					if (source instanceof Spanned) // We need to copy spans if source is spanned
						TextUtils.copySpansFrom((Spanned)source, 0, source.length(), Spanned.class, ch, 0);
					return ch;
				}
				return null;
			}
		};

		// Add that filter to the input field filters
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		ArrayList<InputFilter> filters=new ArrayList<>(Arrays.asList(ev.getFilters()));
		filters.add(filter);
		ev.setFilters(filters.toArray(new InputFilter[filters.size()]));
	}

	// Keep currentValue through configuration changes
	@Override
	public void onSaveInstanceState (Bundle out)
	{
		super.onSaveInstanceState(out);
		out.putParcelable("currentValue", currentValue);
	}

	@Override
	public void onRestoreInstanceState (Bundle in)
	{
		super.onRestoreInstanceState(in);
		currentValue=in.getParcelable("currentValue");
	}

	private double parseCoefficient(String num) throws NumberFormatException // Parses a number of any form into a double coefficient
	{
		// Error handling
		if ("".equals(num) || num.length()<1)
			return 0;

		if (num.charAt(num.length()-1)=='\u03C0')
		{
			if (num.length()==1)
				return Math.PI;
			else if (num.length()==2 && num.charAt(0)=='-') // If the string is two long and the first character is a negation
				return -Math.PI; // Return negative pi
			return parseCoefficient(num.substring(0, num.length()-1))*Math.PI;
		}
		if (num.charAt(num.length()-1)=='e')
		{
			if (num.length()==1)
				return Math.E;
			else if (num.length()==2 && num.charAt(0)=='-') // If the string is two long and the first character is a negation
				return -Math.E; // Return negative e
			return parseCoefficient(num.substring(0, num.length()-1))*Math.E;
		}
		return Double.parseDouble(num);
	}

	private Complex parseComplex(String num)
	{
		// Special string checks
		if (num==null || "".equals(num) || num.length()<1 || num.indexOf("Error", 0)==0 || num.indexOf("ERROR", 0)==0)
			return Complex.ZERO;
		if ("Not prime".equals(num) || "Not prime or composite".equals(num) || "Not Gaussian prime".equals(num))
			return Complex.ZERO;
		if ("Prime".equals(num) || "Gaussian prime".equals(num))
			return Complex.ONE;

		// Handle parentheticals
		if (num.charAt(0)=='(') {
			char ending=num.charAt(num.length()-1);
			if (ending=='\u03C0')
				return Complex.multiply(Complex.PI, parseComplex(num.substring(1, num.length() - 2)));
			else if (ending=='e')
				return Complex.multiply(Complex.E, parseComplex(num.substring(1, num.length()-2)));
		}

		// Start parsing the number
		if (num.contains("\u03C0") || num.contains("e")) {
			// If our number has a character Complex.parseString won't handle, use parseCoefficient-based processing
			String real;
			String imaginary;
			if (num.contains("+")) { // A plus sign is an easy indicator of a two-part number: -a+bi or a+bi
				int n=num.indexOf('+');
				real=num.substring(0, n);
				imaginary=num.substring(n+1, num.length()-1);
				if ("".equals(imaginary)) // Keep "i" from tripping us up
					imaginary="1";
			}
			else if (num.contains("-")) { // A minus sign appears in one of four remaining formats: a-bi, -a-bi, -a, and -bi
				if (num.contains("i")) { // a-bi, -a-bi, or -bi
					int n=num.lastIndexOf('-');
					if (n==0) { // -bi
						real="";
						if ("-i".equals(num)) // Keep "-i" from tripping us up
							imaginary="-1";
						else
							imaginary=num.substring(0, num.length()-1);
					}
					else { // a-bi or -a-bi
						real=num.substring(0, n);
						imaginary=num.substring(n, num.length()-1); // Include the minus sign
						if ("-".equals(imaginary)) // Keep "-i" from tripping us up
							imaginary="-1";
					}
				}
				else { // -a
					imaginary="";
					real=num;
				}
			}
			else { // If there is no sign, it can only be a or bi. Differentiate on i.
				real="";
				imaginary="";
				if ("i".equals(num))
					imaginary="1"; // Keep "i" from tripping us up
				if (num.charAt(num.length()-1)=='i')
					imaginary=num.substring(0, num.length()-1);
				else
					real=num;
			}
			try {
				return new Complex(parseCoefficient(real), parseCoefficient(imaginary));
			}
			catch (NumberFormatException ex) { // We couldn't manage to handle the string
				setText("ERROR: Invalid number");
				View v=findViewById(R.id.mainCalculateButton);
				v.setOnClickListener(null); // Cancel existing computation
				v.setVisibility(View.GONE); // Remove the button
				return Complex.ERROR;
			}
		}

		// If didn't need to perform the relatively expensive parseCoefficient processing...
		try { // Try to use parseString
			return Complex.parseString(num);
		}
		catch (NumberFormatException e) { // We couldn't manage to handle the string
			setText("ERROR: Invalid number");
			View v=findViewById(R.id.mainCalculateButton);
			v.setOnClickListener(null); // Cancel existing computation
			v.setVisibility(View.GONE); // Remove the button
			return Complex.ERROR;
		}
	}

	private String inIntTermsOfPi(double num)
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

	private String inIntTermsOfPi(Complex num)
	{
		if (num.equals(Complex.ZERO)) // Special case: Prevents "0+0i"
			return "0";
		if (Double.isNaN(num.real()) || Double.isNaN(num.imaginary())) // Trap NaNs
			return "ERROR: Non-numeric result."; // This avoids repeating the message.
		if (num.isReal())
			return inIntTermsOfPi(num.real());
		if (num.isImaginary()) {
			if (num.imaginary()==1)
				return "i";
			else if (num.imaginary()==-1)
				return "-i";
			return inIntTermsOfPi(num.imaginary()) + 'i';
		}
		String out=inIntTermsOfPi(num.real());
		if (num.imaginary()>0)
			out+="+";
		if (num.imaginary()==1)
			out+='i';
		else if (num.imaginary()==-1)
			out+="-i";
		else
			out+=inIntTermsOfPi(num.imaginary())+'i';
		return out;
	}

	private String inIntTermsOfE(double num)
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

	private String inIntTermsOfE(Complex num)
	{
		if (num.equals(Complex.ZERO)) // Special case: Prevents "0+0i"
			return "0";
		if (Double.isNaN(num.real()) || Double.isNaN(num.imaginary())) // Trap NaNs
			return "ERROR: Non-numeric result."; // This avoids repeating the message.
		if (num.isReal())
			return inIntTermsOfE(num.real());
		if (num.isImaginary()) {
			if (num.imaginary()==1)
				return "i";
			else if (num.imaginary()==-1)
				return "-i";
			return inIntTermsOfE(num.imaginary()) + 'i';
		}
		String out=inIntTermsOfE(num.real());
		if (num.imaginary()>0)
			out+="+";
		if (num.imaginary()==1)
			out+='i';
		else if (num.imaginary()==-1)
			out+="-i";
		else
			out+=inIntTermsOfE(num.imaginary())+'i';
		return out;
	}

	private String inIntTermsOfAny(double num)
	{
		if (Double.isNaN(num)) // "Last-resort" check
			return "ERROR: Non-numeric result."; // Trap NaN and return a generic error for it.
		// Because of that check, we can guarantee that NaN's will not be floating around for more than one expression.

		String out=inIntTermsOfPi(num);
		if (!out.equals(Double.toString(num)))
			return out;
		else
			return inIntTermsOfE(num);
	}

	private String inIntTermsOfAny(Complex num)
	{
		if (num.equals(Complex.ZERO)) // Special case: Prevents "0+0i"
			return "0";
		if (Double.isNaN(num.real()) || Double.isNaN(num.imaginary())) // Trap NaNs
			return "ERROR: Non-numeric result."; // This avoids repeating the message.
		if (num.isReal())
			return inIntTermsOfAny(num.real());
		if (num.isImaginary()) {
			if (num.imaginary()==1)
				return "i";
			else if (num.imaginary()==-1)
				return "-i";
			return inIntTermsOfAny(num.imaginary()) + 'i';
		}
		String out=inIntTermsOfAny(num.real());
		if (num.imaginary()>0)
			out+="+";
		if (num.imaginary()==1)
			out+='i';
		else if (num.imaginary()==-1)
			out+="-i";
		else
			out+=inIntTermsOfAny(num.imaginary())+'i';
		return out;
	}

	public void zero(View v)
	{
		setZero();
	}

	private void setZero(EditText ev)
	{
		setText("0", ev);
	}

	private void setZero()
	{
		setZero((EditText) findViewById(R.id.mainTextField));
	}

	private void setText(String n, EditText ev)
	{
		ev.setText(n);
		ev.setSelection(0, n.length()); // Ensure the cursor is at the end
	}

	private void setText(String n)
	{
		setText(n, (EditText) findViewById(R.id.mainTextField));
	}

	public void terms(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		setText(inIntTermsOfAny(parseComplex(ev.getText().toString().trim())), ev);
	}

	public void decimal(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		String str=ev.getText().toString().trim();
		if (str.indexOf("Error", 0)==0 || str.indexOf("ERROR", 0)==0)
			setText(Complex.toString(Complex.ZERO));
		else
			setText(Complex.toString(parseComplex(str)), ev);
	}

	private Complex getValue(final EditText ev) // Parses the content of ev into a double.
	{
		return parseComplex(ev.getText().toString().trim());
	}

	private void doCalculate(final EditText ev, OnClickListener ocl) // Common code for buttons that use the mainCalculateButton.
	{
		doCalculate(ev, ocl, Complex.ZERO);
	}

	private void doCalculate(final EditText ev, OnClickListener ocl, Complex n) // Common code for buttons that use the mainCalculateButton, setting the default value to n rather than zero.
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
					setText(inIntTermsOfAny(Complex.round(currentValue).moduloTo(Complex.round(tmp))), ev);
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
					setText(inIntTermsOfAny(Complex.round(tmp).moduloTo(Complex.round(currentValue))), ev);
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
		setText('('+Complex.toString(tmp)+')'+'\u03C0', ev);
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
		double num=m.real();
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
		if (Math.floor(m.real())==m.real() && Math.floor(m.imaginary())==m.imaginary())
		{
			if (m.isReal())
			{
				int n=(int)Math.abs(m.real());
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
				int n=(int)Math.abs(m.imaginary());
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
				double norm=(m.real()*m.real())+(m.imaginary()*m.imaginary());
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

	private boolean isDivisible(int num, int den) {
		return num%den==0;
	}

	private BigInteger fastPow(int pow) // 2 as base
	{
		return BigInteger.ZERO.flipBit(pow);
	}

	public void raise2(View v)
	{
		EditText ev=(EditText)findViewById(R.id.mainTextField);
		Complex num=parseComplex(ev.getText().toString());
		if (num.isReal() && Math.round(num.real())==num.real()) // Integer power. Use the fastpow() and a BigInteger.
			setText(fastPow((int)Math.round(num.real())).toString(), ev);
		else
			setText(Complex.toString(Complex.pow(2, num)), ev);
	}
}
