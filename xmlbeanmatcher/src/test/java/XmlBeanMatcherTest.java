import org.junit.Test;

import java.util.function.BiConsumer;

import static org.junit.Assert.*;

class XmlBeanMatcherTest
{
    private BiConsumer<String, Runnable> assertErrorMessage = (errorMessage, matcher) -> {
        try
        {
            matcher.run();
        }
        catch (AssertionError e)
        {
            assertEquals(errorMessage.replaceAll("\\R", ""), e.getMessage().replaceAll("\\R", ""));
            return;
        }
        throw new AssertionError("Expected assertionError not thrown");
    };

    @Test
    public void shouldErrForUnmatchedString()
    {
       /* assertErrorMessage.accept("Expected:ServiceOrderHeader{Instances=1, getDn=123, getLifetimeId=null, getMsisdn=null, getNewDn=null, getNewMsisdn=null}but was:ServiceOrderHeader{Instances=1, getDn=1234, getLifetimeId=null, getMsisdn=null, getNewDn=null, getNewMsisdn=null}",
                                  () -> new XmlBeanMatcher().match(GeneralServiceOrderRequest.Message.ServiceOrderHeader.class,
                                                                   new ServiceOrderHeaderBuilder().withDn("123").build(),
                                                                   new ServiceOrderHeaderBuilder().withDn("1234").build())
        );*/
    }

    @Test
    public void shouldErrForUnmatchedArraySize()
    {
        /*assertErrorMessage.accept("Expected:but was:UserId{getUserId=D4RTM43K9@ims.bt.com}getAttributeArray=NameValuePair[]{Instances=1, getName=password, getValue=123}",
                                  () -> new XmlBeanMatcher().match(UserId.class,
                                                                   new UserIdBuilder(Private, Block.New)
                                                                       .withUserId("D4RTM43K9@ims.bt.com")
                                                                       .withAttribute("K4_Id", "64").build(),
                                                                   new UserIdBuilder(Private, Block.New)
                                                                       .withUserId("D4RTM43K9@ims.bt.com")
                                                                       .withAttribute("password", "123")
                                                                       .withAttribute("K4_Id", "64").build())
        );*/
    }

    @Test
    public void shouldErrForMissingElement()
    {
      /*  assertErrorMessage.accept("Expected:PacketData{}getNew=PacketData{Instances=1}but was:PacketData{}getOld=PacketData{Instances=1}",
                                  () -> new XmlBeanMatcher().match(GeneralServiceOrderRequest.Message.PacketData.class,
                                                                   new PacketDataBuilder()
                                                                       .initNewPacketData().build(),
                                                                   new PacketDataBuilder()
                                                                       .initOldPacketData().build())
        );*/
    }

    @Test
    public void shouldErrForUnmatchedElement()
    {
/*
        assertErrorMessage.accept("Expected:but was:UserId{getUserId=D4RTM43K9@ims.bt.com}getAttributeArray=NameValuePair[]{Instances=3, getName=K4_Id, getValue=64}",
                                  () -> new XmlBeanMatcher().match(UserId.class,
                                                                   new UserIdBuilder(Private, Block.New)
                                                                       .withUserId("D4RTM43K9@ims.bt.com")
                                                                       .withAttribute("K4_Id", "64")
                                                                       .withAttribute("K4_Id", "64")
                                                                       .build(),
                                                                   new UserIdBuilder(Private, Block.New)
                                                                       .withUserId("D4RTM43K9@ims.bt.com")
                                                                       .withAttribute("K4_Id", "64")
                                                                       .withAttribute("K4_Id", "64")
                                                                       .withAttribute("K4_Id", "64")
                                                                       .build())
        );
*/
    }

    @Test
    public void shouldErrForUnmatchedXMLEnum()
    {
/*
        assertErrorMessage.accept("Expected:BasicFixedService{getAreaCode=null, getChannel=null, getSubscriptionId=null}getEnum=Enum{Enum=Yes, Instances=1}but was:BasicFixedService{getAreaCode=null, getChannel=null, getSubscriptionId=null}getEnum=Enum{Enum=No, Instances=1}",
                                  () -> new XmlBeanMatcher().match(BasicFixedService.class,
                                                                   new BasicFixedServiceBuilder(Block.New)
                                                                       .withEnum("Yes")
                                                                       .build(),
                                                                   new BasicFixedServiceBuilder(Block.New)
                                                                       .withEnum("No")
                                                                       .build())
        );
*/
    }

    @Test
    public void shouldErrForUnmatchedSubArrayRegardlessSorting()
    {
/*
        assertErrorMessage.accept(
            "Expected:InformationServices{}getNew=InformationServices{}getInformationServiceArray=InformationService[]{}getActivationOption=ActivationOption{getService=MPTY}getSubParameterArray=SubParameter[]{Instances=1, getTag=QOS, getValue=1302}but was:InformationServices{}getNew=InformationServices{}getInformationServiceArray=InformationService[]{}getActivationOption=ActivationOption{Instances=1, getService=MPTY}",
            () -> new XmlBeanMatcher().match(GeneralServiceOrderRequest.Message.InformationServices.class,
                                             new InformationServicesBuilder()
                                                 .withNewOldService("UKB", null)
                                                 .withNewOldService("MPTY", null)
                                                 .withNewSubParameter(InstanceCharacteristicNames.QOS.name(), "1302")
                                                 .build(),
                                             new InformationServicesBuilder()
                                                 .withNewOldService("MPTY", null)
                                                 .withNewOldService("UKB", null)
                                                 .build())
        );
*/
    }

    @Test
    public void shouldErrForExtraSubArrayElement()
    {
/*
        assertErrorMessage.accept("Expected:but was:InformationServices{}getNew=InformationServices{}getInformationServiceArray=InformationService[]{}getActivationOption=ActivationOption{Instances=2}",
                                  () -> new XmlBeanMatcher().match(GeneralServiceOrderRequest.Message.InformationServices.class,
                                                                   new InformationServicesBuilder()
                                                                       .withNewOldService("UKB", null)
                                                                       .withNewOldService("MPTY", null)
                                                                       .build(),
                                                                   new InformationServicesBuilder()
                                                                       .withNewOldService("MPTY", null)
                                                                       .withNewOldService("UKB", null)
                                                                       .withNewOldService("UKB", null)
                                                                       .build())
        );
*/
    }

    @Test
    public void shouldErrForUnmatchedEnumArray()
    {
/*
        assertErrorMessage.accept("Expected:CallForwarding{}getTransactionalData=TransactionalData{Enum=Busy, Instances=1, getCallForwardingNumber=VMS}but was:CallForwarding{}getTransactionalData=TransactionalData{Enum=NoReply, Instances=1, getCallForwardingNumber=VMS}",
                                  () -> new XmlBeanMatcher().match(GeneralServiceOrderRequest.Message.CallForwarding.class,
                                                                   new CallForwardingBuilder()
                                                                       .withNewOldNoReply("Yes", null)
                                                                       .withNewOldNotReachable("Yes", null)
                                                                       .withNewOldBusy("Yes", "Yes")
                                                                       .withNewOldUnconditional("Yes", "Yes")
                                                                       .withCallForwardingNumber("VMS")
                                                                       .withCallForwardingCondition("NoReply")
                                                                       .withCallForwardingCondition("Busy")
                                                                       .build(),
                                                                   new CallForwardingBuilder()
                                                                       .withNewOldNoReply("Yes", null)
                                                                       .withNewOldNotReachable("Yes", null)
                                                                       .withNewOldBusy("Yes", "Yes")
                                                                       .withNewOldUnconditional("Yes", "Yes")
                                                                       .withCallForwardingNumber("VMS")
                                                                       .withCallForwardingCondition("NotReachable")
                                                                       .withCallForwardingCondition("NoReply")
                                                                       .build()));
*/
    }

    @Test
    public void shouldErrForMixedEnumAndOtherType()
    {
/*        assertErrorMessage.accept(
            "Expected:BasicService{}getNew=BasicService{getBillCycleDay=1, getBillingAccountNumber=9000032, getChannel=UKBRA, getCreditClass=null, getDisabilityCode=null, getImei=null, getImsi=894400657876543, getPricePlan=Price, getSalesEntity=BTB, getSeamlessReplacementImsi=null, getSimSerialNumber=89443012345678901280, getSuspensionCode=LOST, getTariff=BTBUS001}getDefaultBearer=Enum{Enum=F, Instances=1}but was:BasicService{}getNew=BasicService{getBillCycleDay=1, getBillingAccountNumber=9000032, getChannel=UKBRA, getCreditClass=null, getDisabilityCode=null, getImei=null, getImsi=894400657876543, getPricePlan=Price, getSalesEntity=BTB, getSeamlessReplacementImsi=null, getSimSerialNumber=89443012345678901280, getSuspensionCode=LOST, getTariff=BTBUS001}getDefaultBearer=Enum{Enum=D, Instances=1}",
            () -> new XmlBeanMatcher().match(GeneralServiceOrderRequest.Message.BasicService.class,
                                             new BasicServiceBuilder()
                                                 .withNewOldDefaultBearer("F", "D")
                                                 .withNewOldMethodOfPayment("A", "A")
                                                 .withNewOldChannel("UKBRA", "UKBRA")
                                                 .withNewOldCallerLineId("AW", "AW")
                                                 .withNewOldPricePlan("Price", "Price")
                                                 .withNewOldTariff("BTBUS001", "BTBUS001")
                                                 .withNewOldBillingAccountNumber("9000032", "9000032")
                                                 .withNewOldBillCycleDay("1", "1")
                                                 .withNewOldSimSerialNumber("89443012345678901280", "89443012345678901280")
                                                 .withNewOldImsi("894400657876543", "894400657876543")
                                                 .withNewOldSalesEntity("BTB", "BTB")
                                                 .withNewOldSuspensionCode("LOST", "PLOST")
                                                 .build(),
                                             new BasicServiceBuilder()
                                                 .withNewOldDefaultBearer("D", "D")
                                                 .withNewOldMethodOfPayment("A", "A")
                                                 .withNewOldChannel("UKBRA", "UKBRA")
                                                 .withNewOldCallerLineId("AW", "AW")
                                                 .withNewOldPricePlan("Price", "Price")
                                                 .withNewOldTariff("BTBUS001", "BTBUS001")
                                                 .withNewOldBillingAccountNumber("9000032", "9000032")
                                                 .withNewOldBillCycleDay("1", "1")
                                                 .withNewOldSimSerialNumber("89443012345678901280", "89443012345678901280")
                                                 .withNewOldImsi("894400657876543", "894400657876543")
                                                 .withNewOldSalesEntity("BTB", "BTB")
                                                 .withNewOldSuspensionCode("LOST", "PLOST")
                                                 .build()))*/
        ;
    }

    @Test
    public void shouldErrForExtraElement()
    {
/*
        assertErrorMessage.accept(
            "Expected:but was:BasicFixedService{Instances=7}",
            () -> new XmlBeanMatcher().match(BasicFixedService.class,
                                             null,
                                             new BasicFixedServiceBuilder(Block.New)
                                                 .withEnum("No")
                                                 .withAreaCode("areac")
                                                 .withUserIdList(Lists.newArrayList(new UserIdBuilder(Private, Block.New),
                                                                                    new UserIdBuilder(Public, Block.New)))
                                                 .build()));
*/
    }
}