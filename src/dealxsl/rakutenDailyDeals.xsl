<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:p="http://www.rakuten.com/rss/module/productV2/" version="2.0">
	<xsl:output method="xml" indent="yes" />
	<xsl:preserve-space elements="description" />
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
				<xsl:value-of select="sku"></xsl:value-of>
			</externalId>
			<dealURL>
				<xsl:value-of select="link"></xsl:value-of>
			</dealURL>
			<content>
				<xsl:text>Rakuten has </xsl:text>
				<xsl:value-of select="title"></xsl:value-of>
				<xsl:text> for </xsl:text>
				<xsl:value-of select="price" />
				<xsl:call-template name="listprice">
					<xsl:with-param name="listprice" select="listprice"></xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="linebreak"></xsl:call-template>
				<xsl:call-template name="htmLink2">
					<xsl:with-param name="dest" select="link"></xsl:with-param>
				</xsl:call-template>
			</content>
			<thumbPicURL>
				<xsl:value-of select="imagelink"></xsl:value-of>
			</thumbPicURL>
			<price>
				<xsl:text>$</xsl:text>
				<xsl:value-of select="price"></xsl:value-of>
			</price>
		</thread>
	</xsl:template>
	<xsl:template name="linebreak">
		<xsl:text>&lt;br /&gt;</xsl:text>
	</xsl:template>
	<xsl:template name="listprice">
		<xsl:param name="listprice" select="UNDEFINED" /> <!--default value -->
		<xsl:text>( Was: $</xsl:text>
		<xsl:value-of select="$listprice"></xsl:value-of>
		<xsl:text>)</xsl:text>
	</xsl:template>
	<xsl:template name="htmLink2">
		<xsl:param name="dest" select="UNDEFINED" /> <!--default value -->
		<xsl:text>&lt;a class="boldText" href="</xsl:text>
		<xsl:value-of select="$dest"></xsl:value-of>
		<xsl:text>" target='_blank' &gt; </xsl:text>
		<xsl:text>Link&lt;/a&gt;</xsl:text>
	</xsl:template>

</xsl:stylesheet>


