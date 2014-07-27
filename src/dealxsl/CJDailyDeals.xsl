<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<deals>
			<xsl:apply-templates />
		</deals>
	</xsl:template>
	<xsl:template match="/cj-api/links">
		<totalMatched>
			<xsl:value-of select="./@total-matched"></xsl:value-of>
		</totalMatched>
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="//link">
		<thread>
			<subject>
				<xsl:value-of select="link-name"></xsl:value-of>
			</subject>
			<externalId>
				<xsl:value-of select="destination"></xsl:value-of>
			</externalId>
			<dealURL>
				<xsl:value-of select="link-code-html"></xsl:value-of>
			</dealURL>
			<content>
				<xsl:value-of select="advertiser-name"></xsl:value-of>
				<xsl:text> has </xsl:text>
				<xsl:choose>
					<xsl:when test="link-code-html !=''">
						<xsl:value-of select="substring-before(link-code-html,'&lt;img')"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="description"></xsl:value-of>
						<xsl:text>&lt;br /&gt;</xsl:text>
						<xsl:call-template name="htmLink2">
							<xsl:with-param name="dest" select="destination"></xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</content>
			<advertiserId>
				<xsl:value-of select="advertiser-id"></xsl:value-of>
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


