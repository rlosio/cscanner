def declare_variables(variables, macro):
    @macro
    def since(version):
        "Add a button"
        HTML = """<a href="https://github.com/janoszen/cscanner/releases" target="_blank"><span class="since"><span class="since__text">since</span><span class="since__value">%s</span></span></a>"""
        return HTML % (version)