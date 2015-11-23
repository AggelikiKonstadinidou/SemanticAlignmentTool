package org.cloud4All;

import java.util.ArrayList;
import java.util.List;

public class EASTINApplication {

	private String CommercialName = "";
	private String Database = "";
	private String InsertDate = "";
	private String LastUpdateDate = "";
	private String ManufacturerOriginalFullName = "";
	private String ProductCode = "";
	private int SimilarityLevel = 0;
	private String ThumbnailImageUrl = "";
	private IsoCodePrimary IsoCodePrimary = new IsoCodePrimary();
	private String ManufacturerCountry = "";
	private String ManufacturerWebSiteUrl = "";
	private String EnglishUrl = "";
	private String EnglishDescription = "";
	private List<Features> Features = new ArrayList<Features>();

	public class Features {
		private String FeatureId = "";
		private String FeatureName = "";
		private String FeatureParentName = "";
		private String ValueMax = "";
		private String ValueMin = "";

		/**
		 * @return the featureId
		 */
		public String getFeatureId() {
			return FeatureId;
		}

		/**
		 * @param featureId
		 *            the featureId to set
		 */
		public void setFeatureId(String featureId) {
			FeatureId = featureId;
		}

		/**
		 * @return the featureName
		 */
		public String getFeatureName() {
			return FeatureName;
		}

		/**
		 * @param featureName
		 *            the featureName to set
		 */
		public void setFeatureName(String featureName) {
			FeatureName = featureName;
		}

		/**
		 * @return the featureParentName
		 */
		public String getFeatureParentName() {
			return FeatureParentName;
		}

		/**
		 * @param featureParentName
		 *            the featureParentName to set
		 */
		public void setFeatureParentName(String featureParentName) {
			FeatureParentName = featureParentName;
		}

		/**
		 * @return the valueMax
		 */
		public String getValueMax() {
			return ValueMax;
		}

		/**
		 * @param valueMax
		 *            the valueMax to set
		 */
		public void setValueMax(String valueMax) {
			ValueMax = valueMax;
		}

		/**
		 * @return the valueMin
		 */
		public String getValueMin() {
			return ValueMin;
		}

		/**
		 * @param valueMin
		 *            the valueMin to set
		 */
		public void setValueMin(String valueMin) {
			ValueMin = valueMin;
		}

	}

	public class IsoCodePrimary {
		private String Code = "";
		private String Name = "";

		/**
		 * @return the code
		 */
		public String getCode() {
			return Code;
		}

		/**
		 * @param code
		 *            the code to set
		 */
		public void setCode(String code) {
			Code = code;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return Name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			Name = name;
		}

	}

	/**
	 * @return the commercialName
	 */
	public String getCommercialName() {
		return CommercialName;
	}

	/**
	 * @param commercialName
	 *            the commercialName to set
	 */
	public void setCommercialName(String commercialName) {
		CommercialName = commercialName;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return Database;
	}

	/**
	 * @param database
	 *            the database to set
	 */
	public void setDatabase(String database) {
		Database = database;
	}

	/**
	 * @return the insertDate
	 */
	public String getInsertDate() {
		return InsertDate;
	}

	/**
	 * @param insertDate
	 *            the insertDate to set
	 */
	public void setInsertDate(String insertDate) {
		InsertDate = insertDate;
	}

	/**
	 * @return the lastUpdateDate
	 */
	public String getLastUpdateDate() {
		return LastUpdateDate;
	}

	/**
	 * @param lastUpdateDate
	 *            the lastUpdateDate to set
	 */
	public void setLastUpdateDate(String lastUpdateDate) {
		LastUpdateDate = lastUpdateDate;
	}

	/**
	 * @return the manufacturerOriginalFullName
	 */
	public String getManufacturerOriginalFullName() {
		return ManufacturerOriginalFullName;
	}

	/**
	 * @param manufacturerOriginalFullName
	 *            the manufacturerOriginalFullName to set
	 */
	public void setManufacturerOriginalFullName(
			String manufacturerOriginalFullName) {
		ManufacturerOriginalFullName = manufacturerOriginalFullName;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return ProductCode;
	}

	/**
	 * @param productCode
	 *            the productCode to set
	 */
	public void setProductCode(String productCode) {
		ProductCode = productCode;
	}

	/**
	 * @return the similarityLevel
	 */
	public int getSimilarityLevel() {
		return SimilarityLevel;
	}

	/**
	 * @param similarityLevel
	 *            the similarityLevel to set
	 */
	public void setSimilarityLevel(int similarityLevel) {
		SimilarityLevel = similarityLevel;
	}

	/**
	 * @return the thumbnailImageUrl
	 */
	public String getThumbnailImageUrl() {
		return ThumbnailImageUrl;
	}

	/**
	 * @param thumbnailImageUrl
	 *            the thumbnailImageUrl to set
	 */
	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		ThumbnailImageUrl = thumbnailImageUrl;
	}

	/**
	 * @return the isoCodePrimary
	 */
	public IsoCodePrimary getIsoCodePrimary() {
		return IsoCodePrimary;
	}

	/**
	 * @param isoCodePrimary
	 *            the isoCodePrimary to set
	 */
	public void setIsoCodePrimary(IsoCodePrimary isoCodePrimary) {
		IsoCodePrimary = isoCodePrimary;
	}

	/**
	 * @return the manufacturerCountry
	 */
	public String getManufacturerCountry() {
		return ManufacturerCountry;
	}

	/**
	 * @param manufacturerCountry
	 *            the manufacturerCountry to set
	 */
	public void setManufacturerCountry(String manufacturerCountry) {
		ManufacturerCountry = manufacturerCountry;
	}

	/**
	 * @return the manufacturerWebSiteUrl
	 */
	public String getManufacturerWebSiteUrl() {
		return ManufacturerWebSiteUrl;
	}

	/**
	 * @param manufacturerWebSiteUrl
	 *            the manufacturerWebSiteUrl to set
	 */
	public void setManufacturerWebSiteUrl(String manufacturerWebSiteUrl) {
		ManufacturerWebSiteUrl = manufacturerWebSiteUrl;
	}

	/**
	 * @return the englishUrl
	 */
	public String getEnglishUrl() {
		return EnglishUrl;
	}

	/**
	 * @param englishUrl
	 *            the englishUrl to set
	 */
	public void setEnglishUrl(String englishUrl) {
		EnglishUrl = englishUrl;
	}

	/**
	 * @return the englishDescription
	 */
	public String getEnglishDescription() {
		return EnglishDescription;
	}

	/**
	 * @param englishDescription
	 *            the englishDescription to set
	 */
	public void setEnglishDescription(String englishDescription) {
		EnglishDescription = englishDescription;
	}

	/**
	 * @return the features
	 */
	public List<Features> getFeatures() {
		return Features;
	}

	/**
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(List<Features> features) {
		Features = features;
	}

}
