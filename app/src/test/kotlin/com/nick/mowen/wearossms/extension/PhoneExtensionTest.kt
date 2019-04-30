package com.nick.mowen.wearossms.extension

import org.junit.Test

class PhoneExtensionTest {

    @Test
    fun testStandardFormat() {
        val phone = "(512) 788-0045"
        require(phone.formatPhoneNumber() == "5127880045")
    }

    @Test
    fun testCountryFormat() {
        val phone = "+1-512-788-0045"
        require(phone.formatPhoneNumber() == "5127880045")
    }

    @Test
    fun testSpaceFormat() {
        val phone = "512 788 0045"
        require(phone.formatPhoneNumber() == "5127880045")
    }
}