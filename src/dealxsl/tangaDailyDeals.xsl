<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<deals>
			<xsl:apply-templates />
		</deals>
	</xsl:template>

	<xsl:template match="//item">
		<thread>
			<subject>
				<xsl:value-of select="title"></xsl:value-of>
			</subject>
			<externalId>
				<xsl:value-of select="guid"></xsl:value-of>
			</externalId>
			<dealURL>
				<xsl:value-of select="link"></xsl:value-of>
			</dealURL>
			<content>
				<xsl:text>Tanga has </xsl:text>
				<xsl:value-of select="title"></xsl:value-of>
				<xsl:text><br /></xsl:text>
				<xsl:value-of select="description"></xsl:value-of>
			</content>
			<price>
				<xsl:text>$</xsl:text>
				<xsl:value-of select="price"></xsl:value-of>
			</price>
		</thread>
	</xsl:template>

</xsl:stylesheet>


