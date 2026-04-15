package WalletPackage;

public class AdminCreditManagement {
	
	double threshold;
	double silverCredit;
	double goldCredit;
	double platinumCredit;
	double conversionPercentage;
	public AdminCreditManagement(double threshold,double silver,double gold,double platinum,double convPercentage)
	{
		this.threshold=threshold;
		this.silverCredit=silver;
		this.goldCredit=gold;
		this.conversionPercentage=convPercentage;
		this.platinumCredit=platinum;
	}
	
	public void setThreshold(double threshold2)
	{
		this.threshold=threshold2;
	}
	public void setsilverCredit(double silverCredit)
	{
		this.silverCredit=silverCredit;
	}
	public void setGoldCredit(double goldCredit)
	{
		this.goldCredit=goldCredit;
	}
	public void setPlatinumCredit(double platinumCredit)
	{
		this.platinumCredit=platinumCredit;
	}
	public void setCoversionPercentage(double d) {
		this.conversionPercentage=d;
	}
	public double getThreshold()
	{
		return threshold;
	}

	public double getSilverCredit()
	{
		return silverCredit;
	}

	public double getGoldCredit()
	{
		return goldCredit;
	}

	public double getPlatinumCredit()
	{
		return platinumCredit;
	}
	public double getConversionPercentage()
	{
		return conversionPercentage;
	}
}
