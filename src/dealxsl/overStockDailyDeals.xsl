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
				<xsl:value-of select="link"></xsl:value-of>
			</externalId>
			<dealURL>
				<xsl:value-of select="link"></xsl:value-of>
			</dealURL>
			<content>
				<xsl:text>Over Stock has </xsl:text>
				<xsl:value-of select="title"></xsl:value-of>
				<xsl:text>&lt;br /&gt;</xsl:text>
				<xsl:value-of select="description"></xsl:value-of>
				<xsl:text>&lt;br /&gt;</xsl:text>
				<xsl:call-template name="htmLink2">
					<xsl:with-param name="dest" select="link"></xsl:with-param>
				</xsl:call-template>
			</content>
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


