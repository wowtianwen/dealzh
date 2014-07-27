<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<deals>
			<xsl:apply-templates />
		</deals>
	</xsl:template>
	<xsl:template match="/response/meta/pagination/total_results">
		<totalMatched>
			<xsl:value-of select="."></xsl:value-of>
		</totalMatched>
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="//data">
		<thread>
			<subject>
				<xsl:value-of select="name"></xsl:value-of>
			</subject>
			<externalId>
				<xsl:value-of select="id"></xsl:value-of>
			</externalId>
			<dealURL>
				<xsl:value-of select="tracking_url"></xsl:value-of>
			</dealURL>
			<content>
				<xsl:value-of select="program_name"></xsl:value-of>
				<xsl:text> has </xsl:text>
				<xsl:value-of select="substring-before(code,'&lt;img')"></xsl:value-of>
			</content>
			<advertiserId>
				<xsl:value-of select="program_id"></xsl:value-of>
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


