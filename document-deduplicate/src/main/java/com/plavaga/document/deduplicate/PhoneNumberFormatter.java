package com.plavaga.document.deduplicate;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneNumberFormatter {

	public static void main(String[] args) {

		System.out.println("=================INDIA=================");
		String number = "+91 (0) 8123-92-33-94";
		String countryIso = "IN";

		format(number, countryIso);

		number = "+918123923394";
		format(number, countryIso);

		number = "+91 8123 92 33 94";
		format(number, countryIso);

		number = "0 8123923394";
		format(number, countryIso);

		number = "8123-92-33-94";
		format(number, countryIso);

		number = "8123923394";
		format(number, countryIso);

		number = "+44 7700 900464";
		countryIso = "BR";

		format(number, countryIso);

		number = "7700 900464";
		format(number, countryIso);

		number = "7700900464";
		format(number, countryIso);

		number = "7700-900-464";
		format(number, countryIso);

	}

	private static void format(String number, String countryIso) {

		PhoneNumberUtil util = PhoneNumberUtil.getInstance();
		try {
			PhoneNumber pn = util.parse(number, countryIso);
			System.out.println("\nInput :" + number);
			System.out.println("\nCountry code :" + pn.getCountryCode());
			System.out.println("National Number :" + pn.getNationalNumber());
			System.out.println("Raw Output :" + pn);

			System.out.println("==============================");
		}
		catch (NumberParseException e) {
			e.printStackTrace();
		}
	}

}
