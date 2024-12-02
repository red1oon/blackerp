#!/bin/bash
# fix_packages.sh

# Core errors
sed -i 's/package org.blackerp.shared/package org.blackerp.domain.core.shared/' domain/core/shared/*.kt
sed -i 's/package org.blackerp.error/package org.blackerp.domain.core.error/' domain/core/error/*.kt
sed -i 's/package org.blackerp.plugin/package org.blackerp.domain.core.plugin/' domain/core/plugin/*.kt

# Core domain
sed -i 's/package org.blackerp.domain.event/package org.blackerp.domain.core.event/' domain/core/event/*.kt
sed -i 's/package org.blackerp.domain.metadata/package org.blackerp.domain.core.metadata/' domain/core/metadata/*.kt
sed -i 's/package org.blackerp.domain.values/package org.blackerp.domain.core.values/' domain/core/values/*.kt
sed -i 's/package org.blackerp.domain.tenant/package org.blackerp.domain.core.tenant/' domain/core/tenant/*.kt
sed -i 's/package org.blackerp.validation/package org.blackerp.domain.core.validation/' domain/core/validation/*.kt

# AD modules
sed -i 's/package org.blackerp.domain.table/package org.blackerp.domain.ad.table/' domain/core/ad/table/*.kt
sed -i 's/package org.blackerp.domain.ad.value/package org.blackerp.domain.ad.shared.values/' domain/core/ad/shared/values/*.kt
sed -i 's/package org.blackerp.domain.ad.tab.value/package org.blackerp.domain.ad.tab.values/' domain/core/ad/tab/values/*.kt

# Make executable
chmod +x fix_packages.sh