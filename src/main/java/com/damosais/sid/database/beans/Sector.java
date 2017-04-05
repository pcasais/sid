package com.damosais.sid.database.beans;

/**
 * This class represents the different types of sectors on which the owner of a target (victim) operates
 *
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
public enum Sector {
    //@formatter:off
	ROOT(null, "Root"),
		INDIVIDUAL(ROOT, "Individual"),
		    CRIMINALS(INDIVIDUAL, "Criminals"),
		ASSOCIATIONS(ROOT, "Associations"),
		    CHARITY(ASSOCIATIONS, "Charity"),
		    CHURCHES(ASSOCIATIONS, "Churches"),
		    MEDICAL_ASSOCIATIONS(ASSOCIATIONS, "Medical Associations"),
		    MUSIC_BANDS(ASSOCIATIONS, "Music Bands"),
		    SPORT_CLUBS(ASSOCIATIONS, "Sport Clubs"),
		    TERRORISTS_AND_REBEL_FORCES(ASSOCIATIONS, "Terrorists and rebel forces"),
		    THINK_TANKS_AND_LOBBIES(ASSOCIATIONS, "Think tanks and lobbies"),
		INDUSTRY(ROOT, "Industry"),
			BASIC_MATERIALS(INDUSTRY, "Basic Materials"),
				BASIC_RESOURCES(BASIC_MATERIALS, "Basic Resources"),
					FORESTRY_AND_PAPER(BASIC_RESOURCES, "Forestry & Paper"),
						FORESTRY(FORESTRY_AND_PAPER, "Forestry"),
						PAPER(FORESTRY_AND_PAPER, "Paper"),
					INDUSTRIAL_METALS(BASIC_RESOURCES ,"Industrial Metals"),
						ALUMINIUM(INDUSTRIAL_METALS, "Aluminium"),
						NON_FERROUS_METALS(INDUSTRIAL_METALS, "Nonferrous Metals"),
						STEEL(INDUSTRIAL_METALS, "Steel"),
					MINING(BASIC_RESOURCES, "Mining"),
						COAL(MINING, "Coal"),
						DIAMONDS_AND_GEMSTONES(MINING, "Diamonds & Gemstones"),
						GENERAL_MINING(MINING, "General Mining"),
						GOLD_MINING(MINING, "Gold Mining"),
						PLATINUM_AND_PRECIOUS_METALS(MINING, "Platinium & Precious Metals"),
				CHEMICALS(BASIC_MATERIALS, "Chemicals"),
					COMMODITY_CHEMICALS(CHEMICALS, "Commodity Chemicals"),
					SPECIALTY_CHEMICALS(CHEMICALS, "Specialty Chemicals"),
			CONSUMER_GOODS(INDUSTRY, "Consumer Goods"),
				AUTOMOBILES_AND_PARTS(CONSUMER_GOODS, "Automobiles & Parts"),
					AUTO_PARTS(AUTOMOBILES_AND_PARTS, "Auto Parts"),
					AUTOMOBILES(AUTOMOBILES_AND_PARTS, "Automobiles"),
					TIRES(AUTOMOBILES_AND_PARTS, "Tires"),
				FOOD_AND_BEVERAGE(CONSUMER_GOODS, "Food & Beverage"),
					BEVERAGES(FOOD_AND_BEVERAGE, "Beverages"),
						BREWERS(BEVERAGES, "Brewers"),
						DISTILLERS_AND_VINTNERS(BEVERAGES, "Distillers & Vintners"),
						SOFT_DRINKS(BEVERAGES, "Soft Drinks"),
					FOOD_PRODUCERS(FOOD_AND_BEVERAGE, "Food Producers"),
						FARMING_AND_FISHING(FOOD_PRODUCERS, "Farming & Fishing"),
						FOOD_PRODUCTS(FOOD_PRODUCERS, "Food Products"),
				PERSONAL_AND_HOUSEHOLD_GOODS(CONSUMER_GOODS, "Personal & Household Goods"),
					HOUSEHOLD_GOODS(PERSONAL_AND_HOUSEHOLD_GOODS, "Household Goods"),
						DURABLE_HOUSEHOLD_PRODUCTS(HOUSEHOLD_GOODS, "Durable Household Products"),
						FURNISHINGS(HOUSEHOLD_GOODS, "Furnishings"),
						HOME_CONSTRUCTION(HOUSEHOLD_GOODS, "Home Construction"),
						NON_DURABLE_HOUSEHOLD_PRODUCTS(HOUSEHOLD_GOODS, "Nondurable Household Products"),
					LEISURE_GOODS(PERSONAL_AND_HOUSEHOLD_GOODS, "Leisure Goods"),
						CONSUMER_ELECTRONICS(LEISURE_GOODS, "Consumer Electronics"),
						RECREATIONAL_PRODUCTS(LEISURE_GOODS, "Recreational Products"),
						TOYS(LEISURE_GOODS, "Toys"),
					PERSONAL_GOODS(PERSONAL_AND_HOUSEHOLD_GOODS, "Personal Goods"),
						CLOTHING_AND_ACCESORIES(PERSONAL_GOODS, "Clothing & Accessories"),
						FOOTWEAR(PERSONAL_GOODS, "Footwear"),
						PERSONAL_PRODUCTS(PERSONAL_GOODS, "Personal Products"),
					TOBACCO(PERSONAL_AND_HOUSEHOLD_GOODS, "Tobacco"),
			CONSUMER_SERVICES(INDUSTRY, "Consumer Services"),
				HOUSING(CONSUMER_SERVICES, "Housing"),
					LETTING_AGENCIES(HOUSING, "Letting Agencies"),
				MEDIA(CONSUMER_SERVICES, "Media"),
					BROADCASTING_AND_ENTERTAINMENT(MEDIA, "Broadcasting & Entertainment"),
					    PRESS_AND_MAGAZINES(BROADCASTING_AND_ENTERTAINMENT,"Press and Magazines"),
					    RADIO_CHANNELS(BROADCASTING_AND_ENTERTAINMENT, "Radio Channels"),
					    TELEVISION_CHANNELS(BROADCASTING_AND_ENTERTAINMENT, "Television Channels"),
					MEDIA_AGENCIES(MEDIA, "Media Agencies"),
					PUBLISHING(MEDIA, "Publishing"),
				RETAIL(CONSUMER_SERVICES, "Retail"),
					FOOD_AND_DRUG_RETAILERS(RETAIL, "Food & Drug Retailers"),
						DRUG_RETAILERS(FOOD_AND_DRUG_RETAILERS, "Drug Retailers"),
						FOOD_RETAILERS_AND_WHOLESALERS(FOOD_AND_DRUG_RETAILERS, "Food Retailers & Wholesalers"),
					GENERAL_RETAILERS(RETAIL, "General Retailers"),
						APPAREL_RETAILERS(GENERAL_RETAILERS, "Apparel Retailers"),
						BROADLINE_RETAILERS(GENERAL_RETAILERS, "Broadline Retailers"),
						HOME_IMPROVEMENT_RETAILERS(GENERAL_RETAILERS, "Home Improvement Retailers"),
						SPECIALIZED_CONSUMER_SERVICES(GENERAL_RETAILERS, "Specialized Consumer Services"),
						SPECIALTY_RETAILERS(GENERAL_RETAILERS, "Specialty Retailers"),
				SECURITY_SERVICES(CONSUMER_SERVICES, "Security Services"),
				TRAVEL_AND_LEISURE(CONSUMER_SERVICES, "Travel & Leisure"),
					AIRLINES(TRAVEL_AND_LEISURE, "Airlines"),
					GAMBLING(TRAVEL_AND_LEISURE, "Gambling"),
					HOTELS(TRAVEL_AND_LEISURE, "Hotels"),
					RECREATIONAL_SERVICES(TRAVEL_AND_LEISURE, "Recreational Services"),
					RESTAURANTS_AND_BARS(TRAVEL_AND_LEISURE, "Restaurants & Bars"),
					TRAVEL_AND_TOURISM(TRAVEL_AND_LEISURE, "Travel & Tourism"),
			FINANCIALS(INDUSTRY, "Financials"),
				BANKS(FINANCIALS, "Banks"),
				FINANCIAL_SERVICES(FINANCIALS, "Financial Services"),
					EQUITY_INVESTMENT_INSTRUMENTS(FINANCIAL_SERVICES, "Equity Investment Instruments"),
					GENERAL_FINANCIAL(FINANCIAL_SERVICES, "General Financial"),
						ASSET_MANAGERS(GENERAL_FINANCIAL, "Asset Managers"),
						CONSUMER_FINANCE(GENERAL_FINANCIAL, "Consumer Finance"),
						INVESTMENT_SERVICES(GENERAL_FINANCIAL, "Investment Services"),
						MORTGAGE_FINANCE(GENERAL_FINANCIAL, "Mortgage Finance"),
						SPECIALTY_FINANCE(GENERAL_FINANCIAL, "Specialty Finance"),
					NON_EQUITY_INVESTMENT_INSTRUMENTS(FINANCIAL_SERVICES, "Nonequity Investment Instruments"),
					REAL_ESTATE(FINANCIAL_SERVICES, "Real Estate"),
						REAL_ESTATE_HOLDING(REAL_ESTATE, "Real Estate Holding &"),
						REAL_ESTATE_INVESTMENT_TRUSTS(REAL_ESTATE, "Real Estate Investment Trusts"),
				INSURANCE(FINANCIALS, "Insurance"),
					LIFE_INSURANCE(INSURANCE, "Life Insurance"),
					NON_LIFE_INSURANCE(INSURANCE, "Nonlife Insurance"),
						FULL_LINE_INSURANCE(NON_LIFE_INSURANCE, "Full Line Insurance"),
						INSURANCE_BROKERS(NON_LIFE_INSURANCE, "Insurance Brokers"),
						PROPERTY_AND_CASUALTY_INSURANCE(NON_LIFE_INSURANCE, "Property & Casualty Insurance"),
						REINSURANCE(NON_LIFE_INSURANCE, "Reinsurance"),
			HEALTH_CARE(INDUSTRY, "Health Care"),
				HEALTH_CARE_EQUIPMENT_AND_SERVICES(HEALTH_CARE, "Health Care Equipment & Services"),
					HEALTH_CARE_PROVIDERS(HEALTH_CARE_EQUIPMENT_AND_SERVICES, "Health Care Providers"),
					MEDICAL_EQUIPMENT(HEALTH_CARE_EQUIPMENT_AND_SERVICES, "Medical Equipment"),
					MEDICAL_SUPPLIES(HEALTH_CARE_EQUIPMENT_AND_SERVICES, "Medical Supplies"),
				PHARMACEUTICALS_AND_BIOTECHNOLOGY(HEALTH_CARE, "Pharmaceuticals & Biotechnology"),
					BIOTECHNOLOGY(PHARMACEUTICALS_AND_BIOTECHNOLOGY, "Biotechnology"),
					PHARMACEUTICALS(PHARMACEUTICALS_AND_BIOTECHNOLOGY, "Pharmaceuticals"),
			INDUSTRIALS(INDUSTRY, "Industrials"),
				CONSTRUCTION_AND_MATERIALS(INDUSTRIALS, "Construction & Materials"),
				    ARCHITECTURAL_SERVICES(CONSTRUCTION_AND_MATERIALS, "Architectural Services"),
					BUILDING_MATERIALS_AND_FIXTURES(CONSTRUCTION_AND_MATERIALS, "Building Materials & Fixtures"),
					HEAVY_CONSTRUCTION(CONSTRUCTION_AND_MATERIALS, "Heavy Construction"),
				INDUSTRIAL_GOODS_AND_SERVICES(INDUSTRIALS, "Industrial Goods & Services"),
					AEROSPACE_AND_DEFENSE(INDUSTRIAL_GOODS_AND_SERVICES, "Aerospace & Defense"),
						AEROSPACE(AEROSPACE_AND_DEFENSE, "Aerospace"),
						DEFENSE(AEROSPACE_AND_DEFENSE, "Defense"),
					ELECTRONIC_AND_ELECTRICAL_EQUIPMENT(INDUSTRIAL_GOODS_AND_SERVICES, "Electronic & Electrical Equipment"),
						ELECTRICAL_COMPONENTS(ELECTRONIC_AND_ELECTRICAL_EQUIPMENT, "Electrical Components"),
						ELECTRONIC_EQUIPMENT(ELECTRONIC_AND_ELECTRICAL_EQUIPMENT, "Electronic Equipment"),
					GENERAL_INDUSTRIALS(INDUSTRIAL_GOODS_AND_SERVICES, "General Industrials"),
						CONTAINERS_AND_PACKAGING(GENERAL_INDUSTRIALS, "Containers & Packaging"),
						DIVERSIFIED_INDUSTRIALS(GENERAL_INDUSTRIALS, "Diversified Industrials"),
					INDUSTRIAL_ENGINEERING(INDUSTRIAL_GOODS_AND_SERVICES, "Industrial Engineering"),
						COMMERCIAL_VEHICULES_AND_TRUCKS(INDUSTRIAL_ENGINEERING, "Commercial Vehicules & Trucks"),
						INDUSTRIAL_MACHINERY(INDUSTRIAL_ENGINEERING, "Industrial Machinery"),
					INDUSTRIAL_TRANSPORTATION(INDUSTRIAL_GOODS_AND_SERVICES, "Industrial Transportation"),
						DELIVERY_SERVICES(INDUSTRIAL_TRANSPORTATION, "Delivery Services"),
						MARINE_TRANSPORTATION(INDUSTRIAL_TRANSPORTATION, "Marine Transportation"),
						RAILROADS(INDUSTRIAL_TRANSPORTATION, "Railroads"),
						TRANSPORTATION_SERVICES(INDUSTRIAL_TRANSPORTATION, "Transportation Services"),
						TRUCKING(INDUSTRIAL_TRANSPORTATION, "Trucking"),
					SUPPORT_SERVICES(INDUSTRIAL_GOODS_AND_SERVICES, "Support Services"),
						BUSINESS_SUPPORT_SERVICES(SUPPORT_SERVICES, "Business Support Services"),
						BUSINESS_TRAINING_AND_EMPLOYMENT(SUPPORT_SERVICES, "Business Training & Employment"),
						FINANCIAL_ADMINISTRATION(SUPPORT_SERVICES, "Financial Administration"),
						INDUSTRIAL_SUPPLIERS(SUPPORT_SERVICES, "Industrial Suppliers"),
						MARKETING_AND_EVENTS(SUPPORT_SERVICES, "Marketing & Events"),
						WASTE_AND_DISPOSAL_SERVICES(SUPPORT_SERVICES, "Waste & Disposal Services"),
			OIL_AND_GAS(INDUSTRY, "Oil & Gas"),
				OIL_AND_GAS_PRODUCERS(OIL_AND_GAS, "Oil & Gas Producers"),
					EXPLORATION_AND_PRODUCTION(OIL_AND_GAS_PRODUCERS, "Exploration & Production"),
					INTEGRATED_OIL_AND_GAS(OIL_AND_GAS_PRODUCERS, "Integrated Oil & Gas"),
				OIL_EQUIPMENT_SERVICES_AND_DISTRIBUTION(OIL_AND_GAS, "Oil Equipt, Services & Distribution"),
					OIL_EQUIPMENT_AND_SERVICES(OIL_EQUIPMENT_SERVICES_AND_DISTRIBUTION, "Oil Equipment & Services"),
					PIPELINES(OIL_EQUIPMENT_SERVICES_AND_DISTRIBUTION, "Pipelines"),
			OTHER_SERVICES(INDUSTRY, "Other Services"),
			    CONSULTING_SERVICES(OTHER_SERVICES, "Consulting Services"),
			    FUNERAL_SERVICES(OTHER_SERVICES, "Funeral Services"),
			    LEGAL_SERVICES(OTHER_SERVICES, "Legal Services"),
	            STAFFING_AND_RECRUITING(OTHER_SERVICES, "Staffing and Recruiting"),
	            SPECIALTY_TRAINING(OTHER_SERVICES, "Specialty training"),
			TECHNOLOGY(INDUSTRY, "Technology"),
				SOFTWARE_AND_COMPUTER_SERVICES(TECHNOLOGY, "Software & Computer Services"),
					COMPUTER_SERVICES(SOFTWARE_AND_COMPUTER_SERVICES, "Computer Services"),
					INTERNET(SOFTWARE_AND_COMPUTER_SERVICES, "Internet"),
					    ECOMMERCE(INTERNET, "E-commerce"),
					    PORNOGRAPHY_SITES(INTERNET, "Pornography Sites"),
					    SOCIAL_NETWORKS(INTERNET, "Social Networks"),
					SOFTWARE(SOFTWARE_AND_COMPUTER_SERVICES, "Software"),
				TECHNOLOGY_HARDWARE_AND_EQUIPMENT(TECHNOLOGY, "Technology Hardware & Equipment"),
					COMPUTER_HARDWARE(TECHNOLOGY_HARDWARE_AND_EQUIPMENT, "Computer Hardware"),
					ELECTRONIC_OFFICE_EQUIPMENT(TECHNOLOGY_HARDWARE_AND_EQUIPMENT, "Electronic Office Equipment"),
					SEMICONDUCTORS(TECHNOLOGY_HARDWARE_AND_EQUIPMENT, "Semiconductors"),
					TELECOMMUNICATIONS_EQUIPMENT(TECHNOLOGY_HARDWARE_AND_EQUIPMENT, "Telecommunications Equipment"),
			TELECOMMUNICATIONS(INDUSTRY, "Telecommunications"),
				FIXED_LINE_TELECOMMUNICATIONS(TELECOMMUNICATIONS, "Fixed Line Telecommunications"),
				MOBILE_COMMUNICATIONS(TELECOMMUNICATIONS, "Mobile Telecommunications"),
			UTILITIES(INDUSTRY, "Utilities"),
				ELECTRICITY(UTILITIES, "Electricity"),
				GAS_WATER_AND_MULTIUTILITIES(UTILITIES, "Gas, Water & Multiutilities"),
					GAS_DISTRIBUTION(GAS_WATER_AND_MULTIUTILITIES, "Gas Distribution"),
					MULTIUTILITIES(GAS_WATER_AND_MULTIUTILITIES, "Multiutilities"),
					WATER(GAS_WATER_AND_MULTIUTILITIES, "Water"),
		PUBLIC_SECTOR(ROOT, "Public Sector"),
			ARMED_FORCES(PUBLIC_SECTOR, "Armed Forces"),
				ARMY(ARMED_FORCES, "Army"),
				NAVY(ARMED_FORCES, "Navy"),
				AIR_FORCE(ARMED_FORCES, "Air Force"),
				OTHER_ARMED_FORCES(ARMED_FORCES, "Other Armed Forces"),
					CYBER_FORCES(OTHER_ARMED_FORCES, "Cyber Forces"),
					MILITAR_POLICE(OTHER_ARMED_FORCES, "Militar Police"),
					MISSILE_AND_ROCKET_FORCES(OTHER_ARMED_FORCES, "Missile & Rocket Forces"),
					RESERVE_FORCES(OTHER_ARMED_FORCES, "Reserve Forces"),
					SPECIAL_FORCES(OTHER_ARMED_FORCES, "Special Forces"),
			EMERGENCY_SERVICES(PUBLIC_SECTOR, "Emergency services"),
				EMERGENCY_SYSTEM(EMERGENCY_SERVICES, "Emergency system"),
				FIRE_PROTECTION(EMERGENCY_SERVICES, "Fire protection"),
				POLICE(EMERGENCY_SERVICES, "Police"),
			GOVERNANCE(PUBLIC_SECTOR, "Governance"),
				ECONOMIC_SYSTEM(GOVERNANCE, "Economic system"),
					CENTRAL_BANK(ECONOMIC_SYSTEM, "Central Bank"),
					FINANCIAL_REGULATORS(ECONOMIC_SYSTEM, "Financial regulators"),
					TREASURY(ECONOMIC_SYSTEM, "Treasury"),
				GOVERNMENT_SYSTEM(GOVERNANCE, "Government system"),
					BUSINESS_REGISTRATION(GOVERNMENT_SYSTEM, "Business registration"),
					CIVIL_REGISTRATION(GOVERNMENT_SYSTEM, "Civil registration"),
					LAND_REGISTRATION(GOVERNMENT_SYSTEM, "Land registration"),
					TAX_SYSTEM(GOVERNMENT_SYSTEM, "Tax system"),
				JUDICIAL_SYSTEM(GOVERNANCE, "Judicial system"),
					COURTHOUSES(JUDICIAL_SYSTEM, "Courthouses"),
					PRISONS(JUDICIAL_SYSTEM, "Prisons"),
				LEGISLATIVE_SYSTEM(GOVERNANCE, "Legislative system"),
					CONGRESS(LEGISLATIVE_SYSTEM, "Congress"),
					SENATE(LEGISLATIVE_SYSTEM, "Senate"),
				REGIONAL_BODIES(GOVERNANCE, "Regional bodies"),
					REGIONAL_EXECUTIVE_BODIES(REGIONAL_BODIES, "Regional executive bodies"),
					REGIONAL_LEGISLATIVE_BODIES(REGIONAL_BODIES, "Regional legislative bodies"),
			INFRASTUCTURE(PUBLIC_SECTOR, "Infrastructure"),
				COMMUNICATIONS_INFRASTRUCTURE(INFRASTUCTURE, "Communications Infrastructure"),
					COMMUNICATION_SATELLITES(COMMUNICATIONS_INFRASTRUCTURE, "Communication satellites"),
					POSTAL_SERVICE(COMMUNICATIONS_INFRASTRUCTURE, "Postal service"),
					TELEPHONE_NETWORKS(COMMUNICATIONS_INFRASTRUCTURE, "Telephone networks"),
						FIXED_LINE_NETWORKS(TELEPHONE_NETWORKS, "Fixed line networks"),
						MOBILE_PHONE_NETWORKS(TELEPHONE_NETWORKS, "Mobile phone networks"),
					TELEVISION_AND_RADIO_NETWORKS(COMMUNICATIONS_INFRASTRUCTURE, "Television & Radio network"),
						TELEVISION_NETWORK(TELEVISION_AND_RADIO_NETWORKS, "Television network"),
						RADIO_NETWORK(TELEVISION_AND_RADIO_NETWORKS, "Radio network"),
					UNDERSEA_CABLES(COMMUNICATIONS_INFRASTRUCTURE, "Undersea cables"),
				ENERGY_INFRASTRUCTURE(INFRASTUCTURE, "Energy Infrastructure"),
					ELECTRICAL_POWER_NETWORK(ENERGY_INFRASTRUCTURE, "Electrical Power Network"),
						ELECTRICAL_GRID(ELECTRICAL_POWER_NETWORK, "Electrical Grid"),
						GENERATION_PLANTS(ELECTRICAL_POWER_NETWORK, "Generation Plants"),
					GAS_AND_OIL_PIPELINES(ENERGY_INFRASTRUCTURE, "Gas & Oil Pipelines"),
						GAS_PIPELINES(GAS_AND_OIL_PIPELINES, "Gas Pipelines"),
						OIL_PIPELINES(GAS_AND_OIL_PIPELINES, "Oil Pipelines"),
				MONITORING_INFRASTRUCTURE(INFRASTUCTURE, "Monitoring infrastructure"),
					EARTH_OBSERVATION_SATELLITES(MONITORING_INFRASTRUCTURE, "Earth observation satellites"),
					GLOBAL_POSIOTINING_SYSTEM(MONITORING_INFRASTRUCTURE, "Global positioning system"),
					METEOROLOGICAL_NETWORKS(MONITORING_INFRASTRUCTURE, "Meteorological networks"),
					SISMIC_NETWORKS(MONITORING_INFRASTRUCTURE, "Sismic networks"),
					TIDAL_MONITORING(MONITORING_INFRASTRUCTURE, "Tidal monitoring"),
				SOLID_WASTE_MANAGEMENT(INFRASTUCTURE, "Solid waste management"),
					GARBAGE_COLLECTION(SOLID_WASTE_MANAGEMENT, "Garbage collection"),
					HAZARDOUS_WASTE_DISPOSAL_FACILITIES(SOLID_WASTE_MANAGEMENT, "Hazardous waste disposal facilities"),
					INCINERATORS(SOLID_WASTE_MANAGEMENT, "Incinerators"),
					LANDFILLS(SOLID_WASTE_MANAGEMENT, "Landfills"),
					RECYCLE_CENTRES(SOLID_WASTE_MANAGEMENT, "Recycle centres"),
				SPATIAL_INFRASTRUCTURE(INFRASTUCTURE, "Spatial infrastructure"),
				TRANSPORT_INFRASTRUCTURE(INFRASTUCTURE, "Transport Infrastructure"),
					LAND_TRANSPORT_INFRASTRUCTURE(TRANSPORT_INFRASTRUCTURE, "Land Transpot Infrastructure"),
						MASS_TRANSIT_SYSTEMS(LAND_TRANSPORT_INFRASTRUCTURE, "Mass transit systems"),
							BICYCLE_SYSTEM(MASS_TRANSIT_SYSTEMS, "Bicycle sharing system"),
							BUS_SYSTEM(MASS_TRANSIT_SYSTEMS, "Bus system"),
							COMMUTER_RAIL_SYSTEM(MASS_TRANSIT_SYSTEMS, "Commuter Rail system"),
								RAILWAY_SYSTEM(COMMUTER_RAIL_SYSTEM, "Railway system"),
								SUBWAY_SYSTEM(COMMUTER_RAIL_SYSTEM, "Subway system"),
								TRAMWAY_SYSTEM(COMMUTER_RAIL_SYSTEM, "Tramway system"),
						ROADS_AND_HIGHWAYS(LAND_TRANSPORT_INFRASTRUCTURE, "Roads & Highways"),
					SEA_TRANSPORT_INFRASTRUCTURE(TRANSPORT_INFRASTRUCTURE, "Sea Transport Infrastructure"),
						CANALS_AND_NAVIGABLE_WATERWAYS(SEA_TRANSPORT_INFRASTRUCTURE, "Canals & Navigable waterways"),
						FERRIES(SEA_TRANSPORT_INFRASTRUCTURE, "Ferries"),
						SEAPORTS_AND_LIGHTHOUSES(SEA_TRANSPORT_INFRASTRUCTURE, "Seaports & Lighthouses"),
							SEAPORTS(SEAPORTS_AND_LIGHTHOUSES, "Seaports"),
							LIGHTHOUSES(SEAPORTS_AND_LIGHTHOUSES, "Lighthouses"),
					AIR_TRANSPORT_INFRASTRUCTURE(TRANSPORT_INFRASTRUCTURE, "Air Transport Infrastructure"),
						AIR_NAVIGATIONAL_SYSTEMS(AIR_TRANSPORT_INFRASTRUCTURE, "Air navigational systems"),
						AIRPORTS(AIR_TRANSPORT_INFRASTRUCTURE, "Airports"),
				WATER_MANAGEMENT_INFRASTRUCTURE(INFRASTUCTURE, "Water Management Infrastructure"),
					DRINKING_WATER_SUPPLY(WATER_MANAGEMENT_INFRASTRUCTURE, "Drinking Water Supply"),
					IRRIGATION_SYSTEMS(WATER_MANAGEMENT_INFRASTRUCTURE, "Irrigation systems"),
					SEWAGE_SYSTEM(WATER_MANAGEMENT_INFRASTRUCTURE, "Sewage system"),
					WATER_CONTROL_SYSTEMS(WATER_MANAGEMENT_INFRASTRUCTURE, "Water control systems"),
						COASTAL_MANAGEMENT(WATER_CONTROL_SYSTEMS, "Coastal management"),
						FLOOD_CONTROLS(WATER_CONTROL_SYSTEMS, "Flood controls"),
			PUBLICLY_OWNED_CORPORATIONS(PUBLIC_SECTOR, "Publicly owned corporations"),
			SOCIAL_SERVICES(PUBLIC_SECTOR, "Social services"),
				EDUCATION_SYSTEM(SOCIAL_SERVICES, "Education system"),
					ELEMENTARY_SCHOOLS(EDUCATION_SYSTEM, "Elementary schools"),
					PROFFESIONAL_SCHOOLS(EDUCATION_SYSTEM, "Proffesional schools"),
					RESEARCH_CENTRES(EDUCATION_SYSTEM, "Research centres"),
					SECONDARY_SCHOOLS(EDUCATION_SYSTEM, "Secondary schools"),
					UNIVERSITIES(EDUCATION_SYSTEM, "Universities"),
				HEALTH_CARE_SYSTEM(SOCIAL_SERVICES, "Health care system"),
					EPIDEMY_CONTROL(HEALTH_CARE_SYSTEM, "Epidemy control"),
					HOSPITALS(HEALTH_CARE, "Hospitals"),
				SOCIAL_WELFARE(SOCIAL_SERVICES, "Social welfare"),
					PENSION_SYSTEM(SOCIAL_WELFARE, "Pension system"),
					GOVERMENT_AID(SOCIAL_WELFARE, "Government aid"),
			SPORTS_AND_CULTURAL_SERVICES(PUBLIC_SECTOR, "Sports & cultural services"),
				CULTURAL_INFRASTRUCTURE(SPORTS_AND_CULTURAL_SERVICES, "Cultural infrastructure"),
					CONCERT_HALLS(CULTURAL_INFRASTRUCTURE, "Concert halls"),
					LIBRARIES(CULTURAL_INFRASTRUCTURE, "Libraries"),
					MUSEUMS(CULTURAL_INFRASTRUCTURE, "Museums"),
					STUDIOS(CULTURAL_INFRASTRUCTURE, "Studios"),
						FILM_STUDIOS(STUDIOS, "Film studios"),
						RECORDING_STUDIOS(STUDIOS, "Recording studios"),
					THEATERS(CULTURAL_INFRASTRUCTURE, "Theaters"),
				SPORTS_INFRASTRUCTURE(SPORTS_AND_CULTURAL_SERVICES, "Sports infrastructure"),
					LEAGES_AND_ASSOCIATIONS(SPORTS_INFRASTRUCTURE, "Leagues & associations"),
					SPORTS_FACILITIES(SPORTS_INFRASTRUCTURE, "Sports facilities");
	//@formatter:on

    /**
     * Returns the sector with the matching name
     * 
     * @param name
     *            The name of the sector which is being searched
     * @return The matching sector or null if none matches
     */
    public static Sector getByName(String name) {
        Sector matching = null;
        for (final Sector sector : Sector.values()) {
            if (sector.getName().equalsIgnoreCase(name)) {
                matching = sector;
                break;
            }
        }
        return matching;
    }
    
    private final Sector parent;

    private final String name;

    /**
     * The constructor just requires the parent sector and its name
     * 
     * @param parent
     *            The parent sector
     * @param name
     *            The name of the sector
     */
    private Sector(Sector parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Sector getParent() {
        return parent;
    }

    /**
     * This method returns if a sector is son of a given sector
     * 
     * @param sector
     *            The sector for which we are checking the paternity
     * @return true if the sector is parent (root is parent of root), false otherwise
     */
    public boolean isChildOf(Sector sector) {
        if (sector.getParent() != null && sector.getParent() == sector) {
            return true;
        } else if (sector.getParent() == null) {
            return sector == ROOT;
        } else {
            return sector.getParent().isChildOf(sector);
        }
    }
}