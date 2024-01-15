import java.math.BigInteger;

public class Radix
{
  private static String charList = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()";

  /* LEGEND FOR CHAR LIST IN TERMS OF EACH CHARACTER'S VALUE IN BASE 10
  * 0 to 9 --> 0 to 9 (duh)
  * A to Z --> 10 to 35
  * a to z --> 36 to 61
  * ! to ) --> 62 to 71
  */

  // Returns a String representation of 'val' that is in base 'inputBase' into the base of 'outPut', up to base 72. Unary is not supported for obvious reasons.
  public static String changeBase(String val, int inputBase, int outputBase)
  {
    if (inputBase > 72 || inputBase < 2 || outputBase > 72 || outputBase < 2) {
      throw new IllegalArgumentException("Base is out of range: 1 < base < 73");
    }
    else if (inputBase == outputBase){
      return val;
    }

    String str = "";

    // Adds negative sign if neccesary 
    if (val.indexOf("-") >= 0)  {str += "-";} 

    // If input is an integer.
    if (val.indexOf(".") == -1) {val += "."; return doIntPart(nonDecimalToDecimal(val, inputBase), outputBase);} 

    // Converts the input into base 10 if it is not in it already.
    if (inputBase != 10) {val = nonDecimalToDecimal(val, inputBase);}
    if (outputBase == 10) {return str + val;} 

    return doIntPart(val, outputBase) + "." + doDecimalPart(val, outputBase);
  }

  // Handles values before the decimal place of input value.
  private static String doIntPart(String val, int base)
  {
    String wholeStr = val.substring(val.indexOf("-")+1, val.indexOf("."));

    if (wholeStr.equals("0")) {
      return wholeStr;
    }
    else if (wholeStr.length() < 17) {
      long wholeVal = Long.parseLong(val.substring(val.indexOf("-")+1, val.indexOf(".")));
      return changeBaseWhole(wholeVal, base);
    }

    return changeBaseWhole(wholeStr, base); // This return uses BigInteger
  }

  // Handles values after the decimal place for the input value.
  private static String doDecimalPart(String val, int base) {
    String floats = "";
    double decimalVal = Double.parseDouble(val.substring(val.indexOf(".")));
    double power = 1;
    int count;
    
    // precision can be edited by increasing floats.length() condition values and adding a "&& power > 0" condition
    while(floats.length() < 10 && decimalVal > 0 && power > 0) {
      power /= base;
      count = 0;
     
      while (decimalVal >= power && count < base-1) {
        count++;
        decimalVal -= power;
      }
      floats += charList.charAt(count);
    }

    return floats;
  }

  // Changes the base of a long value
  private static String changeBaseWhole(long num, int base)
  {
    String str = "";

    long power;
    byte count;
    int digits = (int) Math.ceil(Math.log1p(num)/Math.log(base));

    for (int k = digits-1; k >= 0; k--)
    {
      power = (long) Math.pow(base, k);
      count = 0;

      while (num >= power)
      {
        count++;
        num -= power;
      }
      
      str += charList.charAt(count);
    }

    return str;
  }

  // overloaded changeBaseWhole that uses BigInteger instead of a long. This may be inefficient. 
  private static String changeBaseWhole(String num, int base)
  {
    String str = "";
    BigInteger base10 = new BigInteger(num);
    BigInteger power = new BigInteger("1");
    BigInteger baseOther = new BigInteger(Integer.toString(base));

    int digits = 0;
    while(base10.signum() == 1)
    {
      base10 = base10.subtract(power);
      power = power.multiply(baseOther);
      digits++;
    } 
    //System.out.println("Digits:" + digits);

    base10 = new BigInteger(num);
    int count;
    for (int k = digits-1; k >= 0; k--)
    {
      power = power.divide(baseOther); 
      count = 0;

      while (base10.compareTo(power) >= 0)
      {
        count++;
        base10 = base10.subtract(power);
      }
      
      //System.out.println("Count for iteration " + k + ": " + count);
      str += charList.charAt(count);
    }

    if (str.charAt(0) == '0')
    {
      return str.substring(1); // Bad fix for the leading zero bug
    }
    return str;
  }

  // Turns the input 'val' that is in base 'base' into its decimal represntation. 
  // This is done every time the input is not in base 10, just so that it can be made compatible with the other methods. 
  private static String nonDecimalToDecimal(String val, int base) 
  {
    String wholeVal = val.substring(val.indexOf("-")+1, val.indexOf("."));
    String decimalVal = val.substring(val.indexOf(".")+1);

    int maxDigitsForLong = (int) Math.floor(Math.log(9223372036854775807L)/Math.log(base + 0.01)); // That long literal is just max value of a 64 bit unsigned integer
    int length = wholeVal.length()-1;
    BigInteger base10whole = new BigInteger("0");
    String floats = "";

    /*
    // Finds the max digits that can fit into a long in base 10 for all other base. This is to reduce the use of BigInteger.
    switch(base)
    {
      case 2: maxDigitsForLong = 62; break; 
      case 3: maxDigitsForLong = 39; break;
      case 4: maxDigitsForLong = 31; break;
      case 5: maxDigitsForLong = 27; break;
      case 6: maxDigitsForLong = 24; break;
      case 7: maxDigitsForLong = 22; break;
      case 8: maxDigitsForLong = 20; break;
      case 9: maxDigitsForLong = 19; break;
      case 12: maxDigitsForLong = 17; break;
      case 16: maxDigitsForLong = 15; break;
      case 36: maxDigitsForLong = 12; break; 
      default: maxDigitsForLong = 10; break;
    }
    */

    // Whole number portion
    if (maxDigitsForLong >= length+1)
    {
      long power;
      long digit;
      
      for (int k = 0; k <= length; k++)
      {
        power = (long) Math.pow(base, Math.abs(k-length));
        digit = power * charList.indexOf(wholeVal.charAt(k));
        base10whole = base10whole.add(new BigInteger(Long.toString(digit)));
      }
    }
    else
    {
      BigInteger digit = new BigInteger("0");
      BigInteger power;

      for (int k = 0; k <= length; k++)
      {
        power = new BigInteger(Integer.toString(base));
        power = power.pow(Math.abs(k-length));
        digit = power.multiply(new BigInteger(Integer.toString(charList.indexOf(wholeVal.charAt(k))))); // what
        base10whole = base10whole.add(digit);
      }
    }
    
    // Decimal portion
    double decimal = 0.0;
    for (int i = 0; i < decimalVal.length(); i++)
    {
      decimal += (1 / Math.pow(base, i+1) * charList.indexOf(decimalVal.charAt(i)));
    }
    floats = Double.toString(decimal);

    return base10whole.toString() + floats.substring(1);
  }
}