package com.damosais.sid.database.beans;

public enum SocioeconomicVariable {
    //@formatter:off
    GDP_CURRENT_DOLLARS("GDP at current USD $", "USD $", 0d, null, "GDP at purchaser's prices is the sum of gross value added by all resident producers in the economy plus any product taxes and minus any subsidies not included in the value of the products. It is calculated without making deductions for depreciation of fabricated assets or for depletion and degradation of natural resources. Data are in current U.S. dollars. Dollar figures for GDP are converted from domestic currencies using single year official exchange rates. For a few countries where the official exchange rate does not reflect the rate effectively applied to actual foreign exchange transactions, an alternative conversion factor is used.", VariableType.ECONOMIC),
    GDP_CONSTANT_DOLLARS("GDP at constant 2010 USD $", "USD $", 0d, null, "GDP at purchaser's prices is the sum of gross value added by all resident producers in the economy plus any product taxes and minus any subsidies not included in the value of the products. It is calculated without making deductions for depreciation of fabricated", VariableType.ECONOMIC),
    GDP_PER_CAPITA_CURRENT_DOLLARS("GDP per capita at current USD $", "USD $", 0d, null, "GDP per capita is gross domestic product divided by midyear population. GDP is the sum of gross value added by all resident producers in the economy plus any product taxes and minus any subsidies not included in the value of the products. It is calculated without making deductions for depreciation of fabricated assets or for depletion and degradation of natural resources. Data are in current U.S. dollars.", VariableType.ECONOMIC),
    GDP_PER_CAPITA_CONSTANT_DOLLARS("GDP per capita at constant 2010 USD $", "USD $", 0d, null, "GDP per capita is gross domestic product divided by midyear population. GDP is the sum of gross value added by all resident producers in the economy plus any product taxes and minus any subsidies not included in the value of the products. It is calculated without making deductions for depreciation of fabricated assets or for depletion and degradation of natural resources. Data are in constant 2010 U.S. dollars.", VariableType.ECONOMIC),
    GDP_PER_CAPITA_PPP("GDP per capita PPP constant 2011 USD $", "USD $", 0d, null, "GDP per capita based on purchasing power parity (PPP). PPP GDP is gross domestic product converted to international dollars using purchasing power parity rates. An international dollar has the same purchasing power over GDP as the U.S. dollar has in the United States. GDP at purchaser's prices is the sum of gross value added by all resident producers in the economy plus any product taxes and minus any subsidies not included in the value of the products. It is calculated without making deductions for depreciation of fabricated assets or for depletion and degradation of natural resources. Data are in constant 2011 international dollars.", VariableType.ECONOMIC),
    INFLATION("Inflation", "%", null, null, "Inflation as measured by the consumer price index reflects the annual percentage change in the cost to the average consumer of acquiring a basket of goods and services that may be fixed or changed at specified intervals, such as yearly. The Laspeyres formula is generally used.", VariableType.ECONOMIC),
    CORE_INFLATION("Core inflation", "%", null, null, "Core inflation represents the long run trend in the price level. In measuring long run inflation, transitory price changes should be excluded. One way of accomplishing this is by excluding items frequently subject to volatile prices, like food and energy.", VariableType.ECONOMIC),
    INFORMATION_TECHNOLOGY_EXPORTS("Information technology exports", "%", 0d, 100d, "Information and communication technology goods exports include telecommunications, audio and video, computer and related equipment; electronic components; and other information and communication technology goods. Software is excluded.", VariableType.ECONOMIC),
    INNOVATION("Innovation", "points", 0d, 100d, "The Global Innovation Index includes two sub-indices: the Innovation Input Sub-Index and the Innovation Output Sub-Index. The first sub-index is based on five pillars: Institutions, Human capital and research, Infrastructure, Market sophistication, and Business sophistication. The second sub-index is based on two pillars: Knowledge and technology outputs and Creative outputs. Each pillar is divided into sub-pillars and each sub-pillar is composed of individual indicators.", VariableType.ECONOMIC),
    LABOUR_FORCE("Labour force", "M people", 0d, null, "Total labour force comprises people ages 15 and older who meet the International Labour Organization definition of the economically active population: all people who supply labor for the production of goods and services during a specified period. It includes both the employed and the unemployed. While national practices vary in the treatment of such groups as the armed forces and seasonal or part-time workers, in general the labor force includes the armed forces, the unemployed, and first-time job-seekers, but excludes homemakers and other unpaid caregivers and workers in the informal sector.", VariableType.ECONOMIC),
    LENDING_INTEREST_RATE("Lending interest rate", "%", null, null, "Lending rate is the bank rate that usually meets the short- and medium-term financing needs of the private sector. This rate is normally differentiated according to creditworthiness of borrowers and objectives of financing. The terms and conditions attached to these rates differ by country, however, limiting their comparability.", VariableType.ECONOMIC),
    REAL_INTEREST_RATE("Real interest rate", "%", null, null, "Real interest rate is the lending interest rate adjusted for inflation as measured by the GDP deflator. The terms and conditions attached to lending rates differ by country, however, limiting their comparability.", VariableType.ECONOMIC),
    RESEARCH_EXPENDITURE("R&D expenditure", "% of GDP", 0d, 100d, "Expenditures for research and development are current and capital expenditures (both public and private) on creative work undertaken systematically to increase knowledge, including knowledge of humanity, culture, and society, and the use of knowledge for new applications. R&D covers basic research, applied research, and experimental development.", VariableType.ECONOMIC),
    UNEMPLOYMENT_RATE("Unemployment rate", "%", 0d, 100d, "Unemployment refers to the share of the labour force that is without work but available for and seeking employment.", VariableType.ECONOMIC),

    CIVIL_LIBERTIES("Civil liberties", "points", 1d, 7d, "Civil liberties or personal freedoms are personal guarantees and freedoms that the government cannot abridge, either by law or by judicial interpretation, without due process. Though the scope of the term differs between countries, civil liberties may include the freedom from torture, freedom from forced disappearance, freedom of conscience, freedom of press, freedom of religion, freedom of expression, freedom of assembly, the right to security and liberty, freedom of speech, the right to privacy, the right to equal treatment under the law and due process, the right to a fair trial, and the right to life. Other civil liberties include the right to own property, the right to defend oneself, and the right to bodily integrity. Within the distinctions between civil liberties and other types of liberty, distinctions exist between positive liberty/positive rights and negative liberty/negative rights.", VariableType.POLITICAL),
    CORRUPTION_PERCEPTION("Corruption perception", "points", 0d, 100d, "The Corruption Perceptions Index is an indicator of perceptions of public sector corruption, i.e. administrative and political corruption. The indicator values are determined by using information from surveys and assessments of corruption, collected by a variety of reputable institutions.", VariableType.POLITICAL),
    FREEDOM_OF_PRESS("Freedom of press", "points", 0d, 100d, "The Index ranks 180 countries according to the level of freedom available to journalists. It is a snapshot of the media freedom situation based on an evaluation of pluralism, independence of the media, quality of legislative framework and safety of journalists in each country. It does not rank public policies even if governments obviously have a major impact on their country’s ranking", VariableType.POLITICAL),
    GOVERNMENT_EFFECTIVENESS("Government effectiveness", "points", -2.5d, 2.5d, "Government effectiveness captures perceptions of the quality of public services, the quality of the civil service and the degree of its independence from political pressures, the quality of policy formulation and implementation, and the credibility of the government's commitment to such policies.", VariableType.POLITICAL),
    POLITICAL_RIGHTS("Political rights", "points", 1d, 7d, "Civil and political rights are a class of rights that protect individuals' freedom from infringement by governments, social organizations, and private individuals. They ensure one's ability to participate in the civil and political life of the society and state without discrimination or repression. Political rights include natural justice (procedural fairness) in law, such as the rights of the accused, including the right to a fair trial; due process; the right to seek redress or a legal remedy; and rights of participation in civil society and politics such as freedom of association, the right to assemble, the right to petition, the right of self-defense, and the right to vote.", VariableType.POLITICAL),
    POLITICAL_RISK("Political risk", "points", 0d, 100d, "Political risk = Political risk covers the risks of foreign exchange shortages, wars, revolutions, natural disasters and arbitrary government actions. Countries are classified by points (from 0-low risk to 100-high risk) reflecting the intensity of political risk.", VariableType.POLITICAL),
    POLITICAL_STABILITY("Political stability", "points", -2.5d, 2.5d, "Political Stability and Absence of Violence/Terrorism measures perceptions of the likelihood of political instability and/or politically-motivated violence, including terrorism", VariableType.POLITICAL),
    RULE_OF_LAW("Rule of law", "points", -2.5d, 2.5d, "Rule of law captures perceptions of the extent to which agents have confidence in and abide by the rules of society, and in particular the quality of contract enforcement, property rights, the police, and the courts, as well as the likelihood of crime and violence", VariableType.POLITICAL),
    WAR_RISK("War risk", "points", 1d, 7d, " War risk covers the risks of external conflicts and the risks of domestic political violence. Domestic political violence includes terrorism, civil unrest, socio-economic conflicts, racial and ethnic tension and the extreme case of civil war. Countries are classified into seven categories (from 1-low risk to 7-high risk) reflecting the intensity of war risk.", VariableType.POLITICAL),

    HAPPINESS("Happiness", "points", 0d, 10d, "The happiness index uses 6 variables to assess the hapiness of people in a country. Each variable measured reveals a populated-weighted average score on a scale running from 0 to 10 that is tracked over time and compared against other countries. These variables currently include: real GDP per capita, social support, healthy life expectancy, freedom to make life choices, generosity, and perceptions of corruption", VariableType.SOCIAL),
    HUMAN_DEVELOPMENT("Human development", "points", 0d, null, "The Human Development Index measures three basic dimensions of human development: long and healthy life, knowledge, and a decent standard of living. Four indicators are used to calculate the index: life expectancy at birth, mean years of schooling, expected years of schooling, and gross national income per capita.", VariableType.SOCIAL),
    IMPRISIOMENT_RATE("Imprisionment rate", "prisoners per 100K people", 0d, null, "Measures the number of prisoners per 100,000 people", VariableType.SOCIAL),
    INTERNET_SUBSCRIBERS("Internet subscribers", "susbcribers", 0d, null, "Fixed broadband subscriptions refers to fixed subscriptions to high-speed access to the public Internet (a TCP/IP connection), at downstream speeds equal to, or greater than, 256 kbit/s. This includes cable modem, DSL, fiber-to-the-home/building, other fixed (wired)-broadband subscriptions, satellite broadband and terrestrial fixed wireless broadband. This total is measured irrespective of the method of payment. It excludes subscriptions that have access to data communications (including the Internet) via mobile-cellular networks. It should include fixed WiMAX and any other fixed wireless technologies. It includes both residential subscriptions and subscriptions for organizations.", VariableType.SOCIAL),
    INTERNET_SUBSCRIBERS_PER_100_PEOPLE("Internet subscribers per 100 people", "percent", 0d, null, "Fixed broadband subscriptions refers to fixed subscriptions to high-speed access to the public Internet (a TCP/IP connection), at downstream speeds equal to, or greater than, 256 kbit/s. This includes cable modem, DSL, fiber-to-the-home/building, other fixed (wired)-broadband subscriptions, satellite broadband and terrestrial fixed wireless broadband. This total is measured irrespective of the method of payment. It excludes subscriptions that have access to data communications (including the Internet) via mobile-cellular networks. It should include fixed WiMAX and any other fixed wireless technologies. It includes both residential subscriptions and subscriptions for organizations.", VariableType.SOCIAL),
    INTERNET_USERS("Internet users", "%", 0d, 100d, "Internet users are individuals who have used the Internet (from any location) in the last 12 months. Internet can be used via a computer, mobile phone, personal digital assistant, games machine, digital TV etc.", VariableType.SOCIAL),
    LIFE_EXPECTANCY("Life expectancy", "years", 0d, null, "Life expectancy at birth indicates the number of years a newborn infant would live if prevailing patterns of mortality at the time of its birth were to stay the same throughout its life.", VariableType.SOCIAL),
    LITERACY_RATE("Literacy rate", "%", 0d, 100d, "Adult (15+) literacy rate (%). Total is the percentage of the population age 15 and above who can, with understanding, read and write a short, simple statement on their everyday life. Generally, ‘literacy’ also encompasses ‘numeracy’, the ability to make simple arithmetic calculations. This indicator is calculated by dividing the number of literates aged 15 years and over by the corresponding age group population and multiplying the result by 100.", VariableType.SOCIAL),
    POPULATION("Population size", "M people", 0d, null, "Total population is based on the de facto definition of population, which counts all residents regardless of legal status or citizenship--except for refugees not permanently settled in the country of asylum, who are generally considered part of the population of their country of origin. The values shown are midyear estimates.", VariableType.SOCIAL),
    PRIMARY_SCHOOL_ENROLLMENT("Primary school enrollment", "%", 0d, null,  "Gross enrolment ratio. Primary. Total is the total enrollment in primary education, regardless of age, expressed as a percentage of the population of official primary education age. GER can exceed 100% due to the inclusion of over-aged and under-aged students because of early or late school entrance and grade repetition.", VariableType.SOCIAL),
    ROBBERY_RATE("Robbery rate", "robberies per 100K people", 0d, null, "Measures the number of robberies per 100,000 people", VariableType.SOCIAL),
    SECONDARY_SCHOOL_ENROLLMENT("Secondary school enrollment", "%", 0d, null, "Gross enrolment ratio. Secondary. All programmes. Total is the total enrollment in secondary education, regardless of age, expressed as a percentage of the population of official secondary education age. GER can exceed 100% due to the inclusion of over-aged and under-aged students because of early or late school entrance and grade repetition.", VariableType.SOCIAL),
    TERTIARY_SCHOOL_ENROLLMENT("Tertiary school enrollment", "%", 0d, null, "Total enrollment in tertiary education (ISCED 5 to 8), regardless of age, expressed as a percentage of the total population of the five-year age group following on from secondary school leaving.", VariableType.SOCIAL),
    THEFT_RATE("Theft rate", "thefts per 100K people", 0d, null, "Measues the number of thefts per 100,000 people", VariableType.SOCIAL);
    //@formatter:on
    
    private final String name;
    private final String unit;
    private final Double min;
    private final Double max;
    private final String definition;
    private final VariableType type;
    
    private SocioeconomicVariable(String name, String unit, Double min, Double max, String definition, VariableType type) {
        this.name = name;
        this.unit = unit;
        this.min = min;
        this.max = max;
        this.definition = definition;
        this.type = type;
    }
    
    public String getDefinition() {
        return definition;
    }
    
    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public String getName() {
        return name;
    }

    public VariableType getType() {
        return type;
    }
    
    public String getUnit() {
        return unit;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * This method returns the variable that matches the given name
     * @param name The name of the variable
     * @return The matching variable or null if none matches
     */
    public static SocioeconomicVariable getByName(String name){
        SocioeconomicVariable matching = null;
        for(SocioeconomicVariable variable: SocioeconomicVariable.values()){
            if(variable.getName().equalsIgnoreCase(name)){
                matching = variable;
                break;
            }
        }
        return matching;
    }
}