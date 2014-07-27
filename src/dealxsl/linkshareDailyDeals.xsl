<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<deals>
			<xsl:apply-templates />
		</deals>
	</xsl:template>
	<xsl:template match="/couponfeed/TotalPages">
		<totalPages>
			<xsl:value-of select="."></xsl:value-of>
		</totalPages>
	</xsl:template>
	<xsl:template match="//link">
		<thread>
			<subject>
				<xsl:value-of select="offerdescription"></xsl:value-of>
			</subject>
			<externalId>
				<xsl:value-of select="clickurl"></xsl:value-of>
			</externalId>
			<dealURL>
				<xsl:value-of select="concat(clickurl,'&amp;u1=deallover')"></xsl:value-of>
			</dealURL>
			<content>
				<xsl:value-of select="advertisername"></xsl:value-of>
				<xsl:text> has </xsl:text>
				<xsl:value-of select="offerdescription"></xsl:value-of>
				<xsl:text>&lt;br /&gt;</xsl:text>
				<xsl:call-template name="htmLink2">
					<xsl:with-param name="dest"
						select="concat(clickurl,'&amp;u1=deallover')"></xsl:with-param>
				</xsl:call-template>
			</content>
			<advertiserId>
				<xsl:value-of select="advertiserid"></xsl:value-of>
			</advertiserId>
		</thread>
	</xsl:template>
	<xsl:template name="htmLink2">
		<xsl:param name="dest" select="UNDEFINED" /> <!--default value -->
		<xsl:text>&lt;a class="boldText" href="</xsl:text>
		<xsl:value-of select="$dest"></xsl:value-of>
		<xsl:text>" target='_blank' &gt; </xsl:text>
		<xsl:text>Link&lt;/a&gt;</xsl:text>
	</xsl:template>
</xsl:stylesheet>


