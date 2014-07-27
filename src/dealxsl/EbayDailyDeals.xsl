<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" indent="yes"
		cdata-section-elements="content" />

	<xsl:template match="/">
		<deals>
			<xsl:apply-templates />
		</deals>
	</xsl:template>

	<xsl:template match="//Item">
		<thread>
			<subject>
				<xsl:text>[</xsl:text>
				<xsl:value-of select="SavingsRate"></xsl:value-of>
				<xsl:text> Off] </xsl:text>
				<xsl:value-of select="Title"></xsl:value-of>
				<xsl:text>. Now $</xsl:text>
				<xsl:value-of select="ConvertedCurrentPrice"></xsl:value-of>
				<xsl:text> (was  $</xsl:text>
				<xsl:value-of select="MSRP"></xsl:value-of>
				<xsl:text>)</xsl:text>
			</subject>
			<externalId>
				<xsl:value-of select="ItemId"></xsl:value-of>
			</externalId>
			<dealURL>
				<xsl:value-of select="DealURL"></xsl:value-of>
			</dealURL>
			<content>
				<xsl:text>Ebay has </xsl:text>
				<xsl:value-of select="Title"></xsl:value-of>
				<xsl:text>. Now $</xsl:text>
				<xsl:value-of select="ConvertedCurrentPrice"></xsl:value-of>
				<xsl:text> (was  $</xsl:text>
				<xsl:value-of select="MSRP"></xsl:value-of>
				<xsl:text>)</xsl:text>
				<xsl:text> </xsl:text>
				<xsl:value-of select="SavingsRate"></xsl:value-of>
				<xsl:text>Off</xsl:text>
				<xsl:call-template name="linebreak"></xsl:call-template>
				<xsl:call-template name="htmLink2">
					<xsl:with-param name="dest" select="DealURL"></xsl:with-param>
				</xsl:call-template>
			</content>
			<thumbPicURL>
				<xsl:value-of select="PictureURL"></xsl:value-of>
			</thumbPicURL>
			<categoryName>
				<xsl:value-of select="PrimaryCategoryName"></xsl:value-of>
			</categoryName>
			<price>
				<xsl:value-of select="SavingsRate"></xsl:value-of>
				<xsl:text>Off</xsl:text>
			</price>
		</thread>
	</xsl:template>

	<xsl:template name="linebreak">
		<xsl:text>&lt;br /&gt;</xsl:text>
	</xsl:template>
	<xsl:template name="htmLink">
		<xsl:param name="dest" select="UNDEFINED" /> <!--default value -->
		<xsl:element name="a">
			<xsl:attribute name="href">
        <xsl:value-of select="$dest"></xsl:value-of> <!--link target-->
      </xsl:attribute>
			<xsl:text>Link</xsl:text>
		</xsl:element>
	</xsl:template>
	<xsl:template name="htmLink2">
		<xsl:param name="dest" select="UNDEFINED" /> <!--default value -->
		<xsl:text>&lt;a class="boldText" href="</xsl:text>
		<xsl:value-of select="$dest"></xsl:value-of>
		<xsl:text>" target='_blank' &gt; </xsl:text>
		<xsl:text>Link&lt;/a&gt;</xsl:text>
	</xsl:template>
</xsl:stylesheet>


